package com.gurumlab.vocaroutine

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
            val meaningList = item.meaning.split(",")
            val meaning = meaningList.mapIndexed { index, value ->
                "${index + 1}. ${value.trim()}"
            }.joinToString("\n")

            binding.tvVocabulary.text = item.word
            binding.tvMeaning.text = meaning
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