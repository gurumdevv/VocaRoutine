package com.gurumlab.vocaroutine.ui.online

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.databinding.ItemOnlineListBinding
import com.gurumlab.vocaroutine.ui.common.SharedListDiffUtil

class OnlineListAdapter :
    ListAdapter<SharedListInfo, OnlineListAdapter.OnlineListViewHolder>(SharedListDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnlineListViewHolder {
        return OnlineListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OnlineListViewHolder, position: Int) {
        holder.bind(getItem(position).listInfo)
    }

    class OnlineListViewHolder(private val binding: ItemOnlineListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(list: ListInfo) {
            binding.tvListTitle.text = list.title
            binding.tvTotalCount.text =
                binding.root.context.getString(R.string.total_count, list.totalCount)
        }

        companion object {
            fun from(parent: ViewGroup): OnlineListViewHolder {
                return OnlineListViewHolder(
                    ItemOnlineListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}