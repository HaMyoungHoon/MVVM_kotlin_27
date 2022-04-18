package com.mvvm_kotlin_27.screen.recyclerfrag.sub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mvvm_kotlin_27.R
import com.mvvm_kotlin_27.databinding.ListItemLayoutBinding


class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder<ListItemLayoutBinding>>()
{
    inner class RecyclerViewHolder<T : ViewDataBinding> : RecyclerView.ViewHolder
    {
        private var _binding : T?
        constructor(view : View) : super(view)
        {
            DataBindingUtil.bind<T>(view).let {
                _binding = it
            }
        }

        fun binding() : T? = _binding
    }

    inner class ListItemDiffCallback(oldListItemViewModels: ArrayList<RecyclerSetItem>, newListItemViewModels: ArrayList<RecyclerSetItem>) : DiffUtil.Callback()
    {
        private val oldListItemViewModels : ArrayList<RecyclerSetItem> = oldListItemViewModels
        private val newListItemViewModels : ArrayList<RecyclerSetItem> = newListItemViewModels
        override fun getOldListSize(): Int = oldListItemViewModels.size
        override fun getNewListSize(): Int = newListItemViewModels.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
        {
            val oldItem: RecyclerSetItem = oldListItemViewModels[oldItemPosition]
            val newItem: RecyclerSetItem = newListItemViewModels[newItemPosition]
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
        {
            val oldItem: RecyclerSetItem = oldListItemViewModels[oldItemPosition]
            val newItem: RecyclerSetItem = newListItemViewModels[newItemPosition]
            if (oldItem._dateTime != newItem._dateTime) return false
            return true
        }
    }

    var _subList : ObservableArrayList<RecyclerSetItem> = ObservableArrayList()

    fun updateItems(subList : ObservableArrayList<RecyclerSetItem>)
    {
        val callback = ListItemDiffCallback(_subList, subList)
        val result = DiffUtil.calculateDiff(callback)
        _subList.clear()
        _subList.addAll(subList)
        result.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder<ListItemLayoutBinding>, position: Int)
    {
        holder.binding()?.let {
            it.dataContext = _subList[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerViewHolder<ListItemLayoutBinding>
    {
        val inflater = LayoutInflater.from(parent.context)
        return RecyclerViewHolder(inflater.inflate(R.layout.list_item_layout, parent, false))
    }

    override fun getItemCount() : Int = _subList.count()
}