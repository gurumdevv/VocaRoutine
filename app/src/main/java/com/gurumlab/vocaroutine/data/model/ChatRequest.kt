package com.gurumlab.vocaroutine.data.model

import com.squareup.moshi.Json

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    @Json(name = "max_completion_tokens") val maxCompletionTokens: Int = 100
)