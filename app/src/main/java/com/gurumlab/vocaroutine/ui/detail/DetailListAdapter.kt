package com.gurumlab.vocaroutine.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gurumlab.vocaroutine.ui.common.VocabularyDiffUtil
import com.gurumlab.vocaroutine.data.model.Vocabulary
import com.gurumlab.vocaroutine.databinding.ItemDetailListBinding

class DetailListAdapter :
    ListAdapter<Vocabulary, DetailListAdapter.DetailListViewHolder>(VocabularyDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailListViewHolder {
        return DetailListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DetailListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DetailListViewHolder(private val binding: ItemDetailListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Vocabulary) {
            binding.vocabulary = item
        }

        companion object {

            fun from(parent: ViewGroup): DetailListViewHolder {
                return DetailListViewHolder(
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