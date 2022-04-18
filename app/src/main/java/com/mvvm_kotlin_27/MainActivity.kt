package com.mvvm_kotlin_27

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mvvm_kotlin_27.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity()
{
    private val _dataContext : MainActivityViewModel by lazy {
        MainActivityViewModel(this, this, supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val binding : ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.dataContext = _dataContext
    }
}