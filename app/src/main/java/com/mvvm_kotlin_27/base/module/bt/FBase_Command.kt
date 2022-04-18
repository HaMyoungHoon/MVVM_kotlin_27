package com.mvvm_kotlin_27.base.module.bt

import com.mvvm_kotlin_27.data.FConstants
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class FBase_Command
{
    object CMD
    {
        //region not use
        const val NONE                  = -1
        const val REQ_RESERVED          = 1000
        const val ACK_RESERVED          = 1001
        //endregion

        const val REQ_PERSON_INFO_START = 1010
        const val ACK_PERSON_INFO_START = 1011
        const val REQ_PERSON_INFO_ING   = 1020
        const val ACK_PERSON_INFO_ING   = 1021
    }
    object SUB
    {
        const val NONE  = -1
        const val OK    = 0
        const val CONT  = 1
    }
    object COMMON
    {
        const val MAX_MSG_ID = 99999999
    }

    companion object
    {
        const val _stx : Char = (0x02).toChar()
        const val _token : Char = '@'
        const val _splt : Char = '~'
        const val _etx : Char = (0x03).toChar()
    }
    var _msgID : Long = 0
    var _cmd = CMD.REQ_RESERVED
    var _sub = SUB.NONE
    var _arg : MutableList<String> = ArrayList()
    var _expired : Long = 0

    constructor(msg : String? = null)
    {
        if (msg != null)
        {
            parserMsg(msg)
        }

        _expired = System.currentTimeMillis()
    }
    constructor(msg : ByteArray? = null)
    {
        if (msg != null)
        {
            parserMsg(msg)
        }

        _expired = System.currentTimeMillis()
    }

    private fun parserMsg(msg : String)
    {
        if (msg.isEmpty())
        {
            return
        }

        var temp = msg
        temp = temp.replace(_stx.toString(), "")
        temp = temp.replace(_etx.toString(), "")
        val array = temp.split(_token)
        if (array.size < 4)
        {
            return
        }

        _msgID = array[0].toLong()
        _cmd = array[1].toInt()
        _sub = array[2].toInt()
        if (array[3].isNotBlank())
        {
            val arg = decrypt(array[3], FConstants.AES_KEY)
            _arg = arg.split(_splt) as MutableList<String>
        }
    }
    private fun parserMsg(msg : ByteArray)
    {
        parserMsg(msg.toString(Charsets.UTF_8))
    }

    fun getArgStr() : String
    {
        var temp : String = ""
        if (_arg.size > 0)
        {
            _arg.forEach { temp += "$it$_splt" }
            if (temp.last() == _splt)
            {
                temp = temp.removeRange(temp.lastIndex, temp.lastIndex + 1)
            }

            temp = encrypt(temp, FConstants.AES_KEY)
        }
        return temp
    }
    fun addArg(data : String)
    {
        _arg.add(data)
    }
    fun getAS() : String
    {
        return "$_stx$_msgID$_token$_cmd$_token$_sub$_token"
    }
    fun getDF() : String
    {
        return "$_etx"
    }
    fun getString() : String
    {
        return "$_stx$_msgID$_token$_cmd$_token$_sub$_token${getArgStr()}$_etx"
    }
    fun getByte() : ByteArray
    {
        return getString().toByteArray()
    }
    fun isExpired() : Boolean
    {
        val expired = System.currentTimeMillis()
        return ((expired - _expired) > 60 * 1000)
    }

    private fun encrypt(data : String, key : String) : String
    {
        val keySepc = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySepc)

        val crypted = cipher.doFinal(data.toByteArray())

        return crypted.toHex()
    }
    private fun decrypt(data : String, key : String) : String
    {
        val dataArray = data.hexStringToHyteArray()
        val keySepc = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
        cipher.init(Cipher.DECRYPT_MODE, keySepc)

        return String(cipher.doFinal(dataArray))
    }
    private fun ByteArray.toHex() : String
    {
        var ret = ""
        for (by in this)
        {
            ret += String.format("%02X", by)
        }

        return ret
    }
    private fun String.hexStringToHyteArray() : ByteArray
    {
        val len = this.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len)
        {
            data[i / 2] = ((Character.digit(this[i], 16) shl 4) +
                    Character.digit(this[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }
}