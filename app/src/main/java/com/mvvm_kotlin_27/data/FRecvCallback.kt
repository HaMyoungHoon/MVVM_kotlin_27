package com.mvvm_kotlin_27.data

interface FRecvCallback
{
    fun recvMessageB(msg : ByteArray, length : Int)
    fun recvMessageS(msg : String)
}