package com.gurumlab.vocaroutine

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gurumlab.vocaroutine.databinding.ItemMyListBinding

class MyListAdapter(private val clickListener: ListClickListener) :
    ListAdapter<MyList, MyListAdapter.MyListViewHolder>(MyListDiffUtil()) {

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

        fun bind(item: MyList, clickListener: ListClickListener) {
            binding.tvListTitle.text = item.name
            binding.tvTotalCount.text =
                binding.root.context.getString(R.string.total_count, item.totalCount)

            when (item.reviewCount) {
                1 -> {
                    binding.ivDayOne.setImageResource(R.drawable.ic_stamper)
                }

                2 -> {
                    binding.ivDayOne.setImageResource(R.drawable.ic_stamper)
                    binding.ivDayThree.setImageResource(R.drawable.ic_stamper)
                }

                3 -> {
                    binding.ivDayOne.setImageResource(R.drawable.ic_stamper)
                    binding.ivDayThree.setImageResource(R.drawable.ic_stamper)
                    binding.ivDaySeven.setImageResource(R.drawable.ic_stamper)
                }

                else -> {
                    Log.d("reviewCount", "The review count exceeds the range.")
                }
            }

            itemView.setOnClickListener { clickListener.onClick(item) }
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