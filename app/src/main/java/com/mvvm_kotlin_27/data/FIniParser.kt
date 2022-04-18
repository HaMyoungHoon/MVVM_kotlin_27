package com.example.mvvm_kotlin_27.data

import com.mvvm_kotlin_27.data.FCallRef
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.lang.Exception

class FIniParser(filePath: String)
{
    private val _stx = "["
    private val _etx = "]"
    private val _equal = "="
    private val _sp = " "
    private val _ent = "\r\n"

    var _filePath : String = filePath

    fun setFilePath(filePath : String)
    {
        _filePath = filePath
    }
    fun createIniFile(section : String, filePath : String) : Boolean
    {
        var pathBuff = filePath
        if (checkFilePath(filePath))
        {
            return false
        }

        if (_filePath.isEmpty() && filePath.isEmpty())
        {
            return false
        }

        if (pathBuff.isEmpty())
        {
            pathBuff = _filePath
        }

        if (section.isEmpty())
        {
            return false
        }

        try
        {
            val data = "$_stx$section$_etx"
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
    fun setString(cmd : String, value : String, filePath : String) : Boolean
    {
        var buffPath = filePath
        if (!checkFilePath(filePath))
        {
            return false
        }

        if (buffPath.isEmpty())
        {
            buffPath = _filePath
        }

        val buffArray = cmd.split(",")
        if (buffArray.size < 2)
        {
            return false
        }

        return writePrivateProfileString(buffArray[0], buffArray[1], value, buffPath) == 0
    }
    fun setInt(cmd : String, value : Int, filePath : String) : Boolean = setString(cmd, value.toString(), filePath)
    fun setFloat(cmd : String, value : Float, filePath : String) : Boolean = setString(cmd, value.toString(), filePath)

    fun getString(cmd : String, defValue : String, filePath : String) : String
    {
        var buffPath = filePath
        if (!checkFilePath(filePath))
        {
            return defValue
        }

        if (buffPath.isEmpty())
        {
            buffPath = _filePath
        }

        val buffArray = cmd.split(",")
        if (buffArray.size < 2)
        {
            return defValue
        }

        val parseValue = FCallRef<String>()
        parseValue._child = defValue
        getPrivateProfileString(buffArray[0], buffArray[1], defValue, parseValue, 1024, buffPath)

        return parseValue._child!!
    }
    fun getInt(cmd : String, defValue : Int, filePath : String) : Int = getString(cmd, defValue.toString(), filePath).toInt()
    fun getFloat(cmd : String, defValue : Float, filePath : String) : Float = getString(cmd, defValue.toString(), filePath).toFloat()

    private fun checkFilePath(filePath : String = "") : Boolean
    {
        var buff = filePath
        if (_filePath.isEmpty() && filePath.isEmpty())
        {
            return false
        }

        if (filePath.isEmpty())
        {
            buff = _filePath
        }

        val dirPath = buff.substring(0, buff.lastIndexOf("/"))
        if (!File(dirPath).isDirectory)
        {
            File(dirPath).mkdirs()
        }

        return File(buff).exists()
    }
    private fun getPrivateProfileString(section : String, key : String, def : String, ret : FCallRef<String>, size : Int, filePath : String) : Int
    {
        if (section.isEmpty() || section.isBlank())
        {
            return -1
        }
        if (key.isEmpty() || key.isBlank())
        {
            return -1
        }
        if (size <= 0)
        {
            return -1
        }

        var buff : MutableList<String> = ArrayList()
        try
        {
            val fis = FileInputStream(filePath)
            val isr = InputStreamReader(fis)
            buff = isr.readLines().toMutableList()
            isr.close()
            fis.close()
        }
        catch (ex : Exception)
        {
            return -1
        }

        if (buff.isEmpty())
        {
            return -1
        }

        val posSTX = pasSTX(buff, section)
        if (posSTX == -1)
        {
            return -1
        }

        var posETX = pasETX(buff, posSTX)
        if (posETX == -1)
        {
            posETX = buff.size
        }

        for (i in posSTX until posETX)
        {
            if (buff[i].trim().contains("$key$_equal"))
            {
                ret._child = buff[i].substring(buff[i].indexOf(_equal) + 1, buff[i].length)
                break
            }
        }

        while (ret._child!!.indexOf(_sp) == 0)
        {
            ret._child!!.removeRange(0, 1)
        }

        return 0
    }
    private fun writePrivateProfileString(section : String, key : String, value : String, filePath : String) : Int
    {
        if (section.isEmpty() || section.isBlank())
        {
            return -1
        }
        if (key.isEmpty() || key.isBlank())
        {
            return -1
        }

        var ret = -1
        var buff : MutableList<String> = ArrayList()
        try
        {
            val fis = FileInputStream(filePath)
            val isr = InputStreamReader(fis)
            buff = isr.readLines().toMutableList()
            isr.close()
            fis.close()
        }
        catch (ex : Exception)
        {
            return ret
        }

        if (buff.isEmpty())
        {
            return ret
        }

        val posSTX = pasSTX(buff, section)
        if (posSTX == -1)
        {
            ret = -2
            buff.add("$key$_equal$value")
            return saveBuff(buff, ret)
        }

        var posETX = pasETX(buff, posSTX)
        if (posETX == -1)
        {
            posETX = buff.size
        }

        var posKey = -1
        for (i in posSTX until posETX)
        {
            if (buff[i].trim().contains("$key$_equal"))
            {
                posKey = i
                break
            }
        }

        if (posKey == -1)
        {
            ret = -3
            buff.add("$key$_equal$value")
            return saveBuff(buff, ret)
        }

        ret = 0
        buff[posKey] = "${buff[posKey].substring(0, buff[posKey].indexOf(_equal) + 1)}$value"

        return saveBuff(buff, ret)
    }

    private fun pasSTX(buff : List<String>, section : String) : Int
    {
        var posSTX = -1
        for (i in buff)
        {
            if (i == "$_stx$section$_etx")
            {
                posSTX++
                break
            }
            posSTX++
        }

        return posSTX
    }
    private fun pasETX(buff : List<String>, posSTX : Int) : Int
    {
        var posETX = -1
        for (i in posSTX + 1 until buff.size)
        {
            if (buff[i].contains(_stx))
            {
                posETX = i
                break
            }
        }

        return posETX
    }
    private fun saveBuff(buff : List<String>, ret : Int) : Int
    {
        try
        {
            var data = ""
            for (i in buff)
            {
                if (i == _ent)
                {
                    continue
                }
                data = "$data$i$_ent"
            }
            if (data.isEmpty())
            {
                return -1
            }
            val fos = FileOutputStream(_filePath)
            fos.write(data.toByteArray())
            fos.close()
        }
        catch(ex : Exception)
        {
            return -1
        }

        return ret
    }
}