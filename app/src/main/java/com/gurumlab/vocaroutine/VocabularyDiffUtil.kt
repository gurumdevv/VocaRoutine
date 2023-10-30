package com.gurumlab.vocaroutine

import androidx.recyclerview.widget.DiffUtil

class VocabularyDiffUtil: DiffUtil.ItemCallback<Vocabulary>() {
    override fun areItemsTheSame(oldItem: Vocabulary, newItem: Vocabulary): Boolean {
        return oldItem.word == newItem.word
    }

    override fun areContentsTheSame(oldItem: Vocabulary, newItem: Vocabulary): Boolean {
        return oldItem == newItem
    }
}