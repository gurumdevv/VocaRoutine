package com.gurumlab.vocaroutine

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>
) : Parcelable