package com.example.mvvm_kotlin_27.data

import java.io.File

class FXmlParser(filePath: String)
{
    var _filePath : String = filePath

    fun setFilePath(filePath : String)
    {
        _filePath = filePath
    }
    fun createXmlFile(rootNode : String, filePath : String) : Boolean
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

        if (rootNode.isEmpty())
        {
            return false
        }

        try
        {
            FXmlWriter().create(rootNode, pathBuff)?.run {
                this.flush() ?: return false
            } ?: return false
        }
        catch (ex : Exception)
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
        if (buffArray.isEmpty())
        {
            return false
        }

        val xmlDoc = FXmlDocument()
        try
        {
            if (!xmlDoc.load(buffPath))
            {
                return false
            }
            var nodes = xmlDoc.getNode(buffArray[0]) ?: return false
            for (i in 1 until buffArray.size)
            {
                val node = nodes.getChild(buffArray[i])
                nodes = if (node != null)
                {
                    node
                } else
                {
                    val elem = FXmlNode(buffArray[i], "", nodes, nodes.getChild(nodes.getChildSize() - 1))
                    nodes.appendChild(elem)
                    nodes.getChild(buffArray[i])!!
                }
            }
            nodes.setText(value)
        }
        catch (ex : java.lang.Exception)
        {
            return false
        }

        xmlDoc.save()

        return true
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
        if (buffArray.isEmpty())
        {
            return defValue
        }

        var parseValue = defValue
        val xmlDoc = FXmlDocument()
        try
        {
            if(!xmlDoc.load(buffPath))
            {
                return defValue
            }

            var nodes  = xmlDoc.getNode(buffArray[0]) ?: return defValue
            for (i in 1 until  buffArray.size)
            {
                val node = nodes.getChild(buffArray[i]) ?: return defValue
                nodes = node
            }
            if (nodes.getText() != null)
            {
                parseValue = nodes.getText()!!
            }
        }
        catch (ex : java.lang.Exception)
        {
            return defValue
        }

        return parseValue
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
}