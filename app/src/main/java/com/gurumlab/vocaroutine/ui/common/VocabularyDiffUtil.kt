package com.gurumlab.vocaroutine.ui.common

import androidx.recyclerview.widget.DiffUtil
import com.gurumlab.vocaroutine.data.model.Vocabulary

class VocabularyDiffUtil: DiffUtil.ItemCallback<Vocabulary>() {
    override fun areItemsTheSame(oldItem: Vocabulary, newItem: Vocabulary): Boolean {
        return oldItem.word == newItem.word
    }

    override fun areContentsTheSame(oldItem: Vocabulary, newItem: Vocabulary): Boolean {
        return oldItem == newItem
    }
}