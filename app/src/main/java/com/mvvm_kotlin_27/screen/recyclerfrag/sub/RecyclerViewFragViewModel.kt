package com.mvvm_kotlin_27.screen.recyclerfrag.sub

import android.content.Context
import android.view.View
import androidx.databinding.ObservableArrayList
import java.text.SimpleDateFormat
import java.util.*

class RecyclerViewFragViewModel(context : Context)
{
    var listItems : ObservableArrayList<RecyclerSetItem> = ObservableArrayList()
    private var _context = context
    var _view : View? = null

    fun addCommand()
    {
        listItems.add(RecyclerSetItem(_context).apply {
            _dateTime = SimpleDateFormat("HH:mm:ss",  Locale.getDefault()).format(Calendar.getInstance().time)
        })
    }
    fun delCommand()
    {
        _view?.requestFocus()
        if (listItems.count() > 0)
        {
            listItems.removeAt(listItems.lastIndex)
        }
    }
}