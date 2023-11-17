package com.gurumlab.vocaroutine.ui.online

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gurumlab.vocaroutine.ui.common.VocabularyDiffUtil
import com.gurumlab.vocaroutine.data.model.Vocabulary
import com.gurumlab.vocaroutine.databinding.ItemDetailListBinding

class OnlineDetailListAdapter :
    ListAdapter<Vocabulary, OnlineDetailListAdapter.OnlineDetailListViewHolder>(VocabularyDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnlineDetailListViewHolder {
        return OnlineDetailListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OnlineDetailListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OnlineDetailListViewHolder(private val binding: ItemDetailListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Vocabulary) {
            binding.vocabulary = item
        }

        companion object {

            fun from(parent: ViewGroup): OnlineDetailListViewHolder {
                return OnlineDetailListViewHolder(
                    ItemDetailListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}