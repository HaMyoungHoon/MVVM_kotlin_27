package com.mvvm_kotlin_27.base.module.bt

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import com.mvvm_kotlin_27.data.FRecvCallback
import java.io.DataOutputStream
import java.util.*
import kotlin.concurrent.thread

class FBTThread_C(recvcallback: FRecvCallback)
{
    companion object
    {
        private val ACCEPT_TAG = "FBTThread"
    }

    enum class eMODE
    {
        RECV_ST,
        RECV_BY,
    }

    var FRecvCallback : FRecvCallback? = recvcallback
    var _isRun = false
    var _btMode : eMODE = eMODE.RECV_ST
    var _btSocket : BluetoothSocket? = null

    fun destructor()
    {
        close()
    }

    fun setMode(recvMode : eMODE)
    {
        _btMode = recvMode
    }

    fun runRecv()
    {
        while (_isRun)
        {
            if (_btSocket != null)
            {
                if (_btSocket!!.isConnected)
                {
                    try
                    {
//						val recvData = DataInputStream(btSocket!!.inputStream)
                        if (FRecvCallback != null)
                        {
                            val buff = ByteArray(256)
                            val length = _btSocket!!.inputStream.read(buff)
//							recvData.read(buff)
                            if (_btMode == eMODE.RECV_BY)
                            {
                                FRecvCallback!!.recvMessageB(buff, length)
                            }
                            else
                            {
                                val str = String(buff)
                                FRecvCallback!!.recvMessageS(str)
                            }
                        }
                    }
                    catch (ex: Exception)
                    {
//					    isRun = false
                        if (_btSocket != null)
                        {
                            _btSocket!!.close()
//							btSocket = null
                        }
                    }
                }
            }
        }
    }

    fun isBTConnect() : Boolean
    {
        if (_btSocket == null)
        {
            return false
        }

        return _btSocket!!.isConnected
    }
    fun open(adt : BluetoothAdapter, addr : String, uuid: UUID) : Boolean
    {
        try
        {
            _btSocket = adt.getRemoteDevice(addr).createRfcommSocketToServiceRecord(uuid) // UUID.fromString("A8CE83BC-BC99-47EC-9967-440A04FA9800"))
            _btSocket!!.connect()
            if (_btSocket!!.isConnected)
            {
                _isRun = true
                thread(start = true)
                {
                    while(_isRun)
                    {
                        runRecv()
                        Thread.sleep(10)
                    }
                }
            }
        }
        catch (ex : Exception)
        {
            return false
        }

        return true
    }
    fun close()
    {
        _isRun = false
        if (_btSocket != null)
        {
            _btSocket!!.close()
            _btSocket = null
        }
    }

    fun sendMessageB(msg : ByteArray)
    {
        if (_btSocket == null)
        {
            return
        }
        if (!_btSocket!!.isConnected)
        {
            return
        }

        DataOutputStream(_btSocket!!.outputStream).write(msg)
    }

    fun sendMessageS(msg : String)
    {
        if (_btSocket == null)
        {
            return
        }
        if (_btSocket!!.isConnected == false)
        {
            return
        }

        var buff = msg
        val ds = DataOutputStream(_btSocket!!.outputStream)
        while (buff.length > 128)
        {
            ds.writeUTF(buff.substring(0, 127))
            buff = buff.removeRange(0, 127)
        }
        if (buff.isNotEmpty())
        {
            ds.writeUTF(buff)
        }
        // 아 이거 끄면 접속 끊기네
//		ds.close()
    }
}