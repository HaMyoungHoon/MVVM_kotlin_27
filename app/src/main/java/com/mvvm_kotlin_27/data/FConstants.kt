package com.mvvm_kotlin_27.data

import android.Manifest


object FConstants
{
    val REQ_STORAGE_PERM = 100
    val MANI_STORAGE_PERM = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val MENU_EDIT = 100
    val MENU_DEL = 101
    val INTENT_FDATEPICKER = 1000
    val INTENT_FDATEPICKER_SUB = 1001

    val uuid = "BE50DA3A-683C-4333-B61C-2D6B84C47473"
    val AES_KEY = "6574852065748520"
}