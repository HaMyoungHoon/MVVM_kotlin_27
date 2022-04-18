package com.example.mvvm_kotlin_27.data

import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.FileOutputStream
import java.lang.Exception
import java.util.*

class FXmlDocument() : FXmlNode()
{
    init
    {
        _filePath = ""
        _version = "\"1\""
        _encoding = "\"UTF-8\""
    }

    fun load(filePath : String) : Boolean
    {
        _filePath = filePath
        if (_filePath.isEmpty())
        {
            return false
        }

        if (!File(filePath).exists())
        {
            return false
        }

        val chBuff : CharArray
        try
        {
            val fis = FileInputStream(filePath)
            val isr = InputStreamReader(fis)
            chBuff = CharArray(fis.available())
            isr.read(chBuff)
            isr.close()
            fis.close()
        }
        catch (ex : Exception)
        {
            return false
        }

        if (chBuff.isEmpty())
        {
            return false
        }
        var buff = String(chBuff)
        if (buff.isEmpty())
        {
            return false
        }
        buff = buff.replace(_ent, "")
        while (buff.contains(_tab))
        {
            buff = buff.replace(_tab, "")
        }
        val buffArray = buff.split(_stx).toMutableList()
        for (i in 0 until buffArray.size)
        {
            if (buffArray[i].isEmpty())
            {
                continue
            }

            buffArray[i] = _stx + buffArray[i]

            val isBlank = buffArray[i].substring(buffArray[i].indexOf(_etx) + 1, buffArray[i].length)
            if (isBlank.isBlank() && isBlank.contains(_sp))
            {
                buffArray[i] = buffArray[i].replace(_sp, "")
            }
        }

        var prefix = 0
        for (i in buffArray)
        {
            if (!pasHeader(i))
            {
                prefix++
            }
            else
            {
                prefix++
                break
            }
        }

        setThis(buffArray.subList(prefix, buffArray.size), null, null, null)

        return true
    }
    fun save(filePath : String = "") : Boolean
    {
        if (filePath.isNotEmpty())
        {
            _filePath = filePath
        }

        if (_filePath.isEmpty())
        {
            return false
        }

        try
        {
            val data = "${getHeader()}${_ent}${getString(_tabCount)}"
            if (data.isEmpty())
            {
                return false
            }
            val fos = FileOutputStream(_filePath)
            fos.write(data.toByteArray())
            fos.close()
        }
        catch(ex : Exception)
        {
            return false
        }

        return true
    }

    private fun pasHeader(buff : String) : Boolean
    {
        val stx = buff.indexOf("$_stx?xml")
        val etx = buff.indexOf("?$_etx")
        if (stx >= etx || stx == -1 || etx == -1)
        {
            return false
        }

        val verS = buff.toUpperCase(Locale.ROOT).indexOf("VERSION")
        if (verS != -1)
        {
            val verE = buff.indexOf(_sp, verS)
            if (verE != -1)
            {
                _version = buff.toUpperCase(Locale.ROOT).substring(verS, verE)
                _version = _version.replace("VERSION=", "")
            }
        }

        val encS = buff.toUpperCase(Locale.ROOT).indexOf("ENCODING")
        if (encS != -1)
        {
            val encE = buff.indexOf(_sp, encS)
            if (encE != -1)
            {
                _encoding = buff.toUpperCase(Locale.ROOT).substring(encS, encE)
                _encoding = _encoding.replace("ENCODING=", "")
            }
        }

        return true
    }
    private fun getHeader() : String
    {
        val ret = "$_stx?xml version=$_version encoding=$_encoding ?$_etx"
        return ret
    }
}