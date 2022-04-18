package com.example.mvvm_kotlin_27.data

open class FXmlBase
{
    protected val _stx = "<"
    protected val _etx = ">"
    protected val _ent = "\r\n"
    protected val _tab = "\t"
    protected val _sp = " "

    protected lateinit var _filePath : String
    protected lateinit var _version : String
    protected lateinit var _encoding : String
}