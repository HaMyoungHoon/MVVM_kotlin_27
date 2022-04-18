package com.mvvm_kotlin_27.screen.recyclerfrag.sub

import android.app.AlertDialog
import android.content.Context
import androidx.databinding.ObservableField
import com.mvvm_kotlin_27.base.module.dbSQL.FSqliteDataType

class RecyclerSetItem(context: Context) : FSqliteDataType.Companion.SAMPLE_INFO()
{
    private var _context : Context = context
    var _OX : ObservableField<String> = ObservableField(_ox)
    fun oxCommand()
    {
        AlertDialog.Builder(_context).let {
            it.setTitle("OX Select")
            it.setPositiveButton("O") {
                _, _ ->
                _ox = "O"
                _OX.set(_ox)
            }
            it.setNegativeButton("X") {
                _, _ ->
                _ox = "X"
                _OX.set(_ox)
            }
            it.show()
        }
    }
}