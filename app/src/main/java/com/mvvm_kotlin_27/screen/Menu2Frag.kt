package com.mvvm_kotlin_27.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mvvm_kotlin_27.R
import com.mvvm_kotlin_27.databinding.FragMenu2Binding
import com.mvvm_kotlin_27.screen.sub.Menu2FragViewModel

class Menu2Frag : Fragment()
{
    private val _dataContext = Menu2FragViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val binding : FragMenu2Binding = DataBindingUtil.inflate(inflater, R.layout.frag_menu2, container, false)
        binding.dataContext = _dataContext
        return binding.root
    }
}