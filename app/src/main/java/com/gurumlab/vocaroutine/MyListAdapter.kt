package com.gurumlab.vocaroutine

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gurumlab.vocaroutine.databinding.ItemMyListBinding

class MyListAdapter : ListAdapter<MyList, MyListAdapter.MyListViewHolder>(MyListDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyListViewHolder {
        return MyListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MyListViewHolder(private val binding: ItemMyListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyList) {
            binding.tvListTitle.text = item.name
            binding.tvTotalCount.text =
                binding.root.context.getString(R.string.total_count, item.totalCount)
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