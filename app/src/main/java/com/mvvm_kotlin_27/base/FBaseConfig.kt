package com.mvvm_kotlin_27.base

import com.example.mvvm_kotlin_27.data.FFileParser
import com.mvvm_kotlin_27.data.FConstants
import java.io.File

class FBaseConfig()
{
    var loaded : Boolean
    private var _xmlFile : String
    private var _iniFile : String
    var uuid : String
    var address : String

    init
    {
        loaded = false
        _xmlFile = "MVVM_KOTLIN.xml"
        _iniFile = "MVVM_KOTLIN.ini"
        uuid = ""
        address = ""
    }

    fun getConfigPath() : String = "${FBaseFunc.ins.getDocPath()}$_xmlFile"
    fun getIniPath() : String = "${FBaseFunc.ins.getDocPath()}$_iniFile"
    fun checkFile(path : String = getConfigPath()) : Boolean
    {
        return File(path).exists()
    }
    fun createConfig()
    {
        val xmldata = FFileParser(getConfigPath(), FFileParser.MODE.XML)
        xmldata.createFile("MVVM")
        xmldata.setString("MVVM,TONG_SIN,UUID", FConstants.uuid)
        xmldata.setString("MVVM,TONG_SIN,ADDRESS", "DESKTOP-KON3CKC=24:41:8C:D7:80:1F")

        val inidata = FFileParser(getIniPath(), FFileParser.MODE.INI)
        inidata.createFile("MVVM")
        inidata.setString("TONG_SIN,UUID", FConstants.uuid)
        inidata.setString("TONG_SIN,ADDRESS", "DESKTOP-KON3CKC=24:41:8C:D7:80:1F")
    }
    fun loadConfig()
    {
        if (!checkFile())
        {
            createConfig()
        }

        val xmlData = FFileParser(getConfigPath(), FFileParser.MODE.XML)

        uuid = xmlData.getString("MVVM,TONG_SIN,UUID", FConstants.uuid)
        address = xmlData.getString("MVVM,TONG_SIN,ADDRESS", "DESKTOP-KON3CKC=24:41:8C:D7:80:1F")

//        val inidata = FFileParser(getIniPath(), FFileParser.MODE.INI)
//        inidata.getString("TONG_SIN,UUID", FConstants.uuid)
//        inidata.getString("TONG_SIN,ADDRESS", "DESKTOP-KON3CKC=24:41:8C:D7:80:1F")
        loaded = true
    }
    fun saveConfig()
    {
        val xmldata = FFileParser(getConfigPath(), FFileParser.MODE.XML)
        xmldata.setString("MVVM,TONG_SIN,UUID", uuid)
        xmldata.setString("MVVM,TONG_SIN,ADDRESS", address)

        val inidata = FFileParser(getIniPath(), FFileParser.MODE.INI)
        inidata.setString("TONG_SIN,UUID", uuid)
        inidata.setString("TONG_SIN,ADDRESS", address)
    }
}