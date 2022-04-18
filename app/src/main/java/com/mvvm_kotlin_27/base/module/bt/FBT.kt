package com.mvvm_kotlin_27.base.module.bt

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.mvvm_kotlin_27.data.FRecvCallback
import java.util.*

open class FBT() : FRecvCallback
{
    companion object
    {
        private const val REQ_ENABLE_BT = 0
    }

    lateinit var btThread : FBTThread_C
    var btAdapter : BluetoothAdapter? = null
    private var lastErr = ""

    init
    {
        try
        {
            btAdapter = BluetoothAdapter.getDefaultAdapter()
            btThread = FBTThread_C(this)
        }
        catch (ex : Exception)
        {
            lastErr = ex.toString()
        }
    }
    fun destructor()
    {
        btThread.destructor()
    }

    fun setMode(mode : FBTThread_C.eMODE)
    {
        btThread.setMode(mode)
    }
    fun getLastError() : String
    {
        val ret = lastErr
        lastErr = ""
        return ret
    }

    protected fun isBTNull() : Boolean
    {
        if (btAdapter == null)
        {
            return true
        }

        return false
    }
    protected fun isBTEnable() : Boolean
    {
        if (btAdapter == null)
        {
            return false
        }

        return btAdapter!!.isEnabled
    }
    protected fun isBTDiscovery() : Boolean
    {
        if (btAdapter == null)
        {
            return false
        }

        return btAdapter!!.isDiscovering
    }
    protected fun isBTConnected() = btThread.isBTConnect()

    protected fun btConnect(addr : String, uuid : UUID) : Boolean
    {
        if (btEnable())
        {
            return btThread.open(btAdapter!!, addr, uuid)
        }

        return false
    }
    protected fun btDisconnect()
    {
        btThread.close()
    }

    protected fun btEnable() : Boolean
    {
        if (btAdapter == null)
        {
            return false
        }

        return btAdapter!!.enable()
    }
    fun btDisable() : Boolean
    {
        if (btAdapter == null)
        {
            return false
        }

        return btAdapter!!.disable()
    }
    fun btDiscoverStart() : Boolean
    {
        if (btAdapter == null)
        {
            return false
        }

        return btAdapter!!.startDiscovery()
    }
    fun btDiscoverCancel() : Boolean
    {
        if (btAdapter == null)
        {
            return false
        }

        return btAdapter!!.cancelDiscovery()
    }
    fun btGetDevice() : Set<BluetoothDevice>?
    {
        if (btAdapter == null)
        {
            return null
        }

        return btAdapter!!.bondedDevices
    }

    override fun recvMessageS(msg: String)
    {
        if (msg.isEmpty())
            return
    }
    override fun recvMessageB(msg: ByteArray, length : Int)
    {
        if (msg.isEmpty())
            return
    }

    fun sendMessageS(msg : String)
    {
        try
        {
            btThread.sendMessageS(msg)
        }
        catch (ex : Exception)
        {
            return
        }
    }
    fun sendMessageB(msg : ByteArray)
    {
        try
        {
            btThread.sendMessageB(msg)
        }
        catch (ex : Exception)
        {
            return
        }
    }
}