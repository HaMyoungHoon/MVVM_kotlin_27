package com.example.mvvm_kotlin_27.data

import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.FileOutputStream
import java.lang.Exception

class FXmlWriter() : FXmlNode()
{

    fun create(rootNode : String, filePath : String) : FXmlWriter?
    {
        if (filePath.isNotEmpty())
        {
            _filePath = filePath
        }

        try
        {
            setTag(rootNode)
            val data = getHeader()
            val fos = FileOutputStream(_filePath)
            fos.write(data.toByteArray())
            fos.close()
        }
        catch(ex : Exception)
        {
            return null
        }

        return this
    }
    fun writeStartElement(localName : String)
    {
        if (_startTag.isNullOrEmpty())
        {
            _startTag = localName
            _endTag = localName
            return
        }

        setLastChild(FXmlNode(localName, "", null, null))
    }
    fun writeElementString(localName: String)
    {
        setLastChildText(localName, "")
    }
    fun writeElementString(localName : String, value : String)
    {
        setLastChildText(localName, value)
    }
    fun flush() : Boolean
    {
        if (_filePath.isEmpty())
        {
            return false
        }

        val chBuff : CharArray
        try
        {
            val fis = FileInputStream(_filePath)
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

        try
        {
            val data : String = if (chBuff.isNotEmpty())
            {
                "${String(chBuff)}$_ent${getString(_tabCount)}"
            } else
            {
                "${getHeader()}$_ent${getString(_tabCount)}"
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
    private fun getHeader() : String
    {
        return "$_stx?xml version=$_version encoding=$_encoding ?$_etx"
    }
}