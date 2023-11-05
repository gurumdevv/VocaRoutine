package com.gurumlab.vocaroutine.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatContent(
    val index: Int,
    val message: ChatMessage,
    @Json(name = "finish_reason") val finishReason: String
) : Parcelable