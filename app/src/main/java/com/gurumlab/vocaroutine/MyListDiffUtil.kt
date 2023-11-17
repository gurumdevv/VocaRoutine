package com.gurumlab.vocaroutine

import androidx.recyclerview.widget.DiffUtil

class MyListDiffUtil : DiffUtil.ItemCallback<MyList>() {
    override fun areItemsTheSame(oldItem: MyList, newItem: MyList): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: MyList, newItem: MyList): Boolean {
        return oldItem == newItem
    }
}