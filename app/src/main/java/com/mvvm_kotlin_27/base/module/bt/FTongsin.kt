package com.mvvm_kotlin_27.base.module.bt

import com.mvvm_kotlin_27.base.FBaseFunc
import com.mvvm_kotlin_27.base.module.dbSQL.FSqliteDataType
import com.mvvm_kotlin_27.data.FCallRef
import com.mvvm_kotlin_27.data.FDelegateString
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

class FTongsin() : FBT()
{
    private var _thReqPop : Boolean
    private var _thUpload : Boolean
    private var _isDownloading : Boolean
    private var _upCount : Int
    private var _downCount : Int
    private var _msgID : Long
    private var _buff : String
    private var _delegate : FDelegateString?
    private var _recvQ : ReentrantLock
    private var _ack : MutableList<FBase_Command>
    private var _req : Queue<FBase_Command>

    init
    {
        _thReqPop = false
        _thUpload = false
        _isDownloading = false
        _upCount = 0
        _downCount = 0
        _msgID = 0
        _buff = ""
        _delegate = null
        _recvQ = ReentrantLock()
        _ack = ArrayList()
        _req = LinkedList()
        if (!isBTEnable())
        {
            btEnable()
        }
        modeChange(true)
    }

    fun destroy()
    {
        destructor()
        _thReqPop = false
    }
    fun setDelegate(delegate : FDelegateString)
    {
        _delegate = delegate
    }

    fun connect(addr : String, uuid : String) : Boolean
    {
        if (chkConnect())
        {
            return false
        }
        clearVariable()
        return btConnect(addr, UUID.fromString(uuid))
    }
    fun disconnect()
    {
        btDisconnect()
        clearVariable()
    }
    fun clearVariable()
    {
        _thUpload = false
        _isDownloading = false
        _upCount = 0
        _downCount = 0
        _msgID = 0
        _buff = ""
        _req.clear()
    }
    fun chkConnect() = isBTConnected()
    fun chkDiscovering() = isBTDiscovery()
    fun modeChange(byMode : Boolean)
    {
        if (byMode)
        {
            setMode(FBTThread_C.eMODE.RECV_BY)
        }
        else
        {
            setMode(FBTThread_C.eMODE.RECV_ST)
        }
    }
    fun getErr() : String
    {
        return getLastError()
    }

    private fun parseMsg()
    {
        var posSTX = _buff.indexOf(FBase_Command._stx)
        var posETX = _buff.indexOf(FBase_Command._etx)
        while (posSTX != -1 && posETX != -1)
        {
            if (posSTX > posETX)
            {
                _buff = _buff.removeRange(0, posETX + 1)
                posSTX = _buff.indexOf(FBase_Command._stx)
                posETX = _buff.indexOf(FBase_Command._etx)
                continue
            }

            val sizeOfMsg = posETX - posSTX + 1
            if (sizeOfMsg <= 0)
                return

            val msg = FBase_Command(_buff.substring(posSTX, posETX))
            _recvQ.lock()
            if (msg._cmd % 2 == 1)
            {
                _ack.removeIf {it.isExpired()}
                _ack.add(msg)
            }
            else
            {
                _req.add(msg)
            }
            _recvQ.unlock()
            _buff = _buff.removeRange(posSTX, posETX + 1)
            posSTX = _buff.indexOf(FBase_Command._stx)
            posETX = _buff.indexOf(FBase_Command._etx)
        }
    }
    private fun getMsgID() : Long
    {
        if (_msgID > FBase_Command.COMMON.MAX_MSG_ID)
        {
            _msgID = 0
        }
        return _msgID++
    }
    private fun setMsgID(msgID : Long)
    {
        _msgID = msgID
    }

    override fun recvMessageB(msg: ByteArray, length : Int)
    {
        if (msg.isEmpty())
            return

        _buff += msg.copyOf(length).toString(Charsets.UTF_8)
        parseMsg()
    }
    override fun recvMessageS(msg: String)
    {
        if (msg.isEmpty())
            return

        _buff += msg
        parseMsg()
    }

    private fun waitReply(recvData : FCallRef<FBase_Command>, timeOut : Long = 60 * 1000) : Boolean
    {
        if (timeOut <= 0)
        {
            return true
        }

        var isRoll = true
        var ret = false
        val currentTime = System.currentTimeMillis()
        var elapsedTime = System.currentTimeMillis()
        while (isRoll)
        {
            if (elapsedTime - currentTime >= timeOut)
            {
                break
            }
            _recvQ.lock()
            if (!isBTConnected() && _ack.size == 0)
            {
                isRoll = false
            }
            _ack.forEach{it ->
                if (!it.isExpired())
                {
                    if (it._cmd == recvData._child!!._cmd)
                    {
                        recvData._child = it
                        _ack.remove(it)
                        ret = true
                        isRoll = false
                    }
                }
            }
            _ack.removeIf{it.isExpired()}
            _recvQ.unlock()
            Thread.sleep(100)
            elapsedTime = System.currentTimeMillis()
        }
        return ret
    }

    private fun whatsTheMatterWithYou(reqData : FBase_Command)
    {
        when (reqData._cmd)
        {
            FBase_Command.CMD.REQ_PERSON_INFO_ING ->                   ack_PersonInfoIng(reqData)
        }
    }

    //region person info
    fun req_PersonInfo() : Boolean
    {
        if (_isDownloading)
        {
            _delegate?.delegate("다른 다운로드가 진행 중입니다.")
            return false
        }

        val sendMsg = FBase_Command("")
        sendMsg._msgID = getMsgID()
        sendMsg._cmd = FBase_Command.CMD.REQ_PERSON_INFO_START
        sendMsg._sub = FBase_Command.SUB.OK
        val recvMsg = FCallRef<FBase_Command>()
        recvMsg._child = FBase_Command("")
        recvMsg._child!!._cmd = FBase_Command.CMD.ACK_PERSON_INFO_START
        sendMessageB(sendMsg.getByte())
        val ret = waitReply(recvMsg)
        if (!ret)
        {
            return ret
        }

        if (recvMsg._child == null)
        {
            return ret
        }

        for (i in recvMsg._child!!._arg)
        {
            val msg = "받을 개수 : $i"
            _delegate?.delegate(msg)
            if (i == "0")
            {
                _isDownloading = false
            }
            break
        }

        return ret
    }
    private fun ack_PersonInfoIng(reqData : FBase_Command)
    {
        val sendMsg = FBase_Command("")
        sendMsg._msgID = getMsgID()
        sendMsg._cmd = FBase_Command.CMD.ACK_PERSON_INFO_ING
        sendMsg._sub = FBase_Command.SUB.OK
        sendMessageB(sendMsg.getByte())
        val ret = reqData._sub
        if (ret == FBase_Command.SUB.NONE)
        {
            _delegate?.delegate("download 실패")
            _downCount = 0
            _isDownloading = false
            return
        }

        _isDownloading = true
        _downCount++
        if (_downCount % 10 == 0)
        {
            _delegate?.delegate("$_downCount 번째 데이터 다운..")
        }

        val buff = FSqliteDataType.Companion.PERSON_INFO()
        for (i in reqData._arg)
        {

            if (buff.setString(i) && !buff.isEmpty())
            {
                FBaseFunc.ins.insertPerson(buff, true)
            }
        }

        if (ret == FBase_Command.SUB.OK)
        {
            _delegate?.delegate("download 종료")
            _downCount = 0
            _isDownloading = false
        }
    }
    //endregion
}