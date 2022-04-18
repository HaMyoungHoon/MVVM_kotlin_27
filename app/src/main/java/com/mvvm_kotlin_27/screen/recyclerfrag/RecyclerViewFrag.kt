package com.mvvm_kotlin_27.screen.recyclerfrag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mvvm_kotlin_27.R
import com.mvvm_kotlin_27.databinding.FragRecyclerviewBinding
import com.mvvm_kotlin_27.screen.recyclerfrag.sub.RecyclerAdapter
import com.mvvm_kotlin_27.screen.recyclerfrag.sub.RecyclerSetItem
import com.mvvm_kotlin_27.screen.recyclerfrag.sub.RecyclerViewFragViewModel


class RecyclerViewFrag : Fragment()
{
    private val _dataContext : RecyclerViewFragViewModel by lazy {
        RecyclerViewFragViewModel(this.context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val binding : FragRecyclerviewBinding = DataBindingUtil.inflate(inflater, R.layout.frag_recyclerview, container, false)
        binding.dataContext = _dataContext
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        _dataContext._view = view
    }

    companion object
    {
        @JvmStatic @BindingAdapter("items")
        fun setItems(recyclerView: RecyclerView, listItem: ObservableArrayList<RecyclerSetItem>)
        {
            if (recyclerView.adapter == null)
            {
                recyclerView.adapter = RecyclerAdapter()
            }

            (recyclerView.adapter as RecyclerAdapter).updateItems(listItem)
        }
    }
}