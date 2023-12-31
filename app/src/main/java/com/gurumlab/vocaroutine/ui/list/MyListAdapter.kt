package com.gurumlab.vocaroutine.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gurumlab.vocaroutine.ui.common.ListClickListener
import com.gurumlab.vocaroutine.ui.common.ListDiffUtil
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.databinding.ItemMyListBinding

class MyListAdapter(private val clickListener: ListClickListener) :
    ListAdapter<ListInfo, MyListAdapter.MyListViewHolder>(ListDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyListViewHolder {
        return MyListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyListViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class MyListViewHolder(private val binding: ItemMyListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(list: ListInfo, clickListener: ListClickListener) {
            binding.list = list
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): MyListViewHolder {
                return MyListViewHolder(
                    ItemMyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
        }
    }
}