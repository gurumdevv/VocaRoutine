package com.gurumlab.vocaroutine.ui.online

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.databinding.ItemOnlineListBinding
import com.gurumlab.vocaroutine.ui.common.ListClickListener
import com.gurumlab.vocaroutine.ui.common.PlusClickListener
import com.gurumlab.vocaroutine.ui.common.SharedListDiffUtil

class OnlineListAdapter(
    private val clickListener: ListClickListener,
    private val plusClickListener: PlusClickListener
) :
    ListAdapter<SharedListInfo, OnlineListAdapter.OnlineListViewHolder>(SharedListDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): OnlineListViewHolder {
        return OnlineListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OnlineListViewHolder, position: Int) {
        holder.bind(getItem(position).listInfo, clickListener, plusClickListener)
    }

    class OnlineListViewHolder(private val binding: ItemOnlineListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            list: ListInfo,
            clickListener: ListClickListener,
            plusClickListener: PlusClickListener
        ) {
            binding.list = list
            binding.clickListener = clickListener
            binding.plusClickListener = plusClickListener
        }

        companion object {
            fun from(parent: ViewGroup): OnlineListViewHolder {
                return OnlineListViewHolder(
                    ItemOnlineListBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }
}