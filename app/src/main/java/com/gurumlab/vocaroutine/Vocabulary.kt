package com.gurumlab.vocaroutine

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Vocabulary(
    val word: String,
    val meaning: String
) : Parcelable