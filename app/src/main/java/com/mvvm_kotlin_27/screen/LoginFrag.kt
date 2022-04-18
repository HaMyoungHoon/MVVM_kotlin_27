package com.mvvm_kotlin_27.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mvvm_kotlin_27.R
import com.mvvm_kotlin_27.data.FDelegateVoid
import com.mvvm_kotlin_27.screen.sub.LoginFragViewModel
import com.mvvm_kotlin_27.databinding.FragLoginBinding

class LoginFrag(delegate : FDelegateVoid) : Fragment()
{
    private val _dataContext = LoginFragViewModel(delegate)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val binding : FragLoginBinding = DataBindingUtil.inflate(inflater, R.layout.frag_login, container, false)
        binding.dataContext = _dataContext
        return binding.root
    }
}