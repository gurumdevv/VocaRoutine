package com.gurumlab.vocaroutine.ui.common

import androidx.recyclerview.widget.DiffUtil
import com.gurumlab.vocaroutine.data.model.ListInfo

class MyListDiffUtil : DiffUtil.ItemCallback<ListInfo>() {
    override fun areItemsTheSame(oldItem: ListInfo, newItem: ListInfo): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: ListInfo, newItem: ListInfo): Boolean {
        return oldItem == newItem
    }
}