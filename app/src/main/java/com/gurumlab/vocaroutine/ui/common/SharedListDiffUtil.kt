package com.gurumlab.vocaroutine.ui.common

import androidx.recyclerview.widget.DiffUtil
import com.gurumlab.vocaroutine.data.model.SharedListInfo

class SharedListDiffUtil: DiffUtil.ItemCallback<SharedListInfo>() {
    override fun areItemsTheSame(oldItem: SharedListInfo, newItem: SharedListInfo): Boolean {
        return oldItem.identifier == newItem.identifier
    }

    override fun areContentsTheSame(oldItem: SharedListInfo, newItem: SharedListInfo): Boolean {
        return oldItem == newItem
    }
}