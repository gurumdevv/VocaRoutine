package com.gurumlab.vocaroutine

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatResponse(
    val id: String,
    @Json(name = "object") val objectName: String,
    val created: Int,
    val model: String,
    val choices: List<ChatContent>,
    val usage: TokenInfo
) : Parcelable