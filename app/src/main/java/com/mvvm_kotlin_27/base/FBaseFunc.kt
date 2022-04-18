package com.mvvm_kotlin_27.base

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Environment
import com.mvvm_kotlin_27.base.module.bt.FTongsin
import com.mvvm_kotlin_27.base.module.dbSQL.FSqlite
import com.mvvm_kotlin_27.base.module.dbSQL.FSqliteDataType
import com.mvvm_kotlin_27.data.FDelegateString
import java.util.*

class FBaseFunc()
{
    companion object
    {
        val ins = FBaseFunc()
    }

    var sqlite : FSqlite? = null
        private set
    var config : FBaseConfig
        private set
    private var _personInfo : MutableList<FSqliteDataType.Companion.PERSON_INFO>
    var loginInfo : FSqliteDataType.Companion.PERSON_INFO
        private set
    private var _tongsin : FTongsin? = null

    init
    {
        config = FBaseConfig()
        _personInfo = ArrayList()
        loginInfo = FSqliteDataType.Companion.PERSON_INFO()
        _tongsin = FTongsin()
    }

    // region tongsin
    fun openTongsin(delegate : FDelegateString)
    {
        if (_tongsin == null)
        {
            _tongsin = FTongsin()
        }
        _tongsin?.setDelegate(delegate)
    }
    fun closeTongsin()
    {
        if (_tongsin != null)
        {
            _tongsin?.disconnect()
            _tongsin?.destroy()
        }
    }
    fun connection(address : String, uuid : String) =_tongsin?.connect(address, uuid) ?: false
    fun disConnection()
    {
        _tongsin?.disconnect()
    }
    fun chkConnection() = _tongsin?.chkConnect() ?: false
    fun req_PersonInfo() = _tongsin?.req_PersonInfo() ?: false
    fun chkDiscovering()
    {
        if (!_tongsin?.chkDiscovering()!!)
        {
            _tongsin?.btDiscoverStart()
        }
    }
    fun getDevice() = _tongsin?.btGetDevice()
    fun discoverCancel()
    {
        _tongsin?.btDiscoverCancel()
    }
    //endregion


    fun initSql(context : Context)
    {
        if (sqlite == null)
        {
            sqlite = FSqlite(context)
            sqlite!!.insertPerson(FSqliteDataType.Companion.PERSON_INFO().apply {
                this._id = "master"
                this._name = "master"
                this._pw = "mhha"
                this._part = "master"
                this._level = "65535"
            })
        }

        reCallPersonInfo()
    }
    fun reCallPersonInfo()
    {
        sqlite!!.getPerson()?.let {
            with(it)
            {
                while (moveToNext())
                {
                    _personInfo.add(FSqliteDataType.Companion.PERSON_INFO(it))
                }
            }
        }
    }
    fun loginConfirm(id : String, pw : String) : String
    {
        val person = _personInfo.find { it._id == id } ?: return "아이디가 없습니다!"
        var temp = person._pw
        if (person._id == "master")
        {
            val currentTime = Calendar.getInstance().time
            temp += java.text.SimpleDateFormat("ddmm", Locale.getDefault()).format(currentTime)
        }

        if (temp != pw)
        {
            return "비밀번호가 다릅니다!"
        }

        loginInfo = person

        return ""
    }
    fun logout()
    {
        loginInfo = FSqliteDataType.Companion.PERSON_INFO()
    }

    //region person db
    fun insertPerson(data : FSqliteDataType.Companion.PERSON_INFO, isBT : Boolean = false)
    {
        if (data.isEmpty())
        {
            return
        }

        sqlite?.insertPerson(data, isBT)
    }
    //endregion


    fun getDocPath() : String
    {
        return "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath}/"
    }
    fun getFilePath() : String
    {
        val currentTime = Calendar.getInstance().time
        val fileName = "TestDB_${SimpleDateFormat("yyyyMMdd_hhmmss", Locale.getDefault()).format(currentTime)}.db"

        return "${getDocPath()}/$fileName"
    }
}