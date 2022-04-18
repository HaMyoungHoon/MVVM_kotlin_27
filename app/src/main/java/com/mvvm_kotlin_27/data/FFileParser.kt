package com.example.mvvm_kotlin_27.data

class FFileParser(filePath: String, mode: MODE = MODE.INI)
{
    enum class MODE
    {
        INI,
        XML
    }

    private var _mode : MODE = mode
    private var _filePath : String = filePath
    private var _iniParser : FIniParser = FIniParser(filePath)
    private var _xmlParser : FXmlParser = FXmlParser(filePath)

    fun createFile(root : String, filePath : String = "")
    {
        var pathBuff = filePath
        if (pathBuff.isEmpty())
        {
            pathBuff = _filePath
        }
        when (_mode)
        {
            MODE.INI -> _iniParser.createIniFile(root, pathBuff)
            MODE.XML -> _xmlParser.createXmlFile(root, pathBuff)
        }
    }
    fun setString(key : String, value : String, filePath : String = "")
    {
        when (_mode)
        {
            MODE.INI -> _iniParser.setString(key, value, filePath)
            MODE.XML -> _xmlParser.setString(key, value, filePath)
        }
    }
    fun setInt(key : String, value : Int, filePath : String = "")
    {
        when (_mode)
        {
            MODE.INI -> _iniParser.setInt(key, value, filePath)
            MODE.XML -> _xmlParser.setInt(key, value, filePath)
        }
    }
    fun setFloat(key : String, value : Float, filePath : String = "")
    {
        when (_mode)
        {
            MODE.INI -> _iniParser.setFloat(key, value, filePath)
            MODE.XML -> _xmlParser.setFloat(key, value, filePath)
        }
    }

    fun getString(key : String, defValue : String, filePath : String = "") : String
    {
        return when (_mode)
        {
            MODE.INI -> _iniParser.getString(key, defValue, filePath)
            MODE.XML -> _xmlParser.getString(key, defValue, filePath)
        }
    }
    fun getInt(key : String, defValue : Int, filePath : String = "") : Int
    {
        return when (_mode)
        {
            MODE.INI -> _iniParser.getInt(key, defValue, filePath)
            MODE.XML -> _xmlParser.getInt(key, defValue, filePath)
        }
    }
    fun getFloat(key : String, defValue : Float, filePath : String = "") : Float
    {
        return when (_mode)
        {
            MODE.INI -> _iniParser.getFloat(key, defValue, filePath)
            MODE.XML -> _xmlParser.getFloat(key, defValue, filePath)
        }
    }
}