package com.gurumlab.vocaroutine

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatMessage(
    val role: String,
    val content: String
) : Parcelable