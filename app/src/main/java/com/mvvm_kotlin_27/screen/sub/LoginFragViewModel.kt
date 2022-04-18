package com.mvvm_kotlin_27.screen.sub

import androidx.databinding.ObservableField
import com.mvvm_kotlin_27.base.FBaseFunc
import com.mvvm_kotlin_27.data.FDelegateVoid

class LoginFragViewModel(delegate: FDelegateVoid)
{
    val id : ObservableField<String> = ObservableField()
    val pw : ObservableField<String> = ObservableField()
    val log : ObservableField<String> = ObservableField()
    private val _delegate : FDelegateVoid = delegate

    init
    {
        id.set("")
        pw.set("")
        log.set("")
    }

    fun loginCommand()
    {
        if (id.get().isNullOrBlank())
        {
            addLog("아이디를 입력해주세요!")
            return
        }
        if (pw.get().isNullOrBlank())
        {
            addLog("비밀번호를 입력해주세요!")
            return
        }

        val ret = FBaseFunc.ins.loginConfirm(id.get()!!, pw.get()!!)
        if (ret.isNotEmpty())
        {
            addLog(ret)
            return
        }

        id.set("")
        pw.set("")
        log.set("")
        
        _delegate.delegate()
    }
    fun personCallCommand()
    {

    }

    private fun addLog(data : String)
    {
        log.set("$data\n${log.get()}\n")
    }
}