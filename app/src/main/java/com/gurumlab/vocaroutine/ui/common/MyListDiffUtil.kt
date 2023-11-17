package com.gurumlab.vocaroutine.ui.common

import androidx.recyclerview.widget.DiffUtil
import com.gurumlab.vocaroutine.data.model.MyList

class MyListDiffUtil : DiffUtil.ItemCallback<MyList>() {
    override fun areItemsTheSame(oldItem: MyList, newItem: MyList): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: MyList, newItem: MyList): Boolean {
        return oldItem == newItem
    }
}