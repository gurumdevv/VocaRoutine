package com.gurumlab.vocaroutine.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Vocabulary(
    val word: String,
    val meaning: String,
    val etymology: String,
) : Parcelable