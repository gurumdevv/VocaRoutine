package com.gurumlab.vocaroutine.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val firstReview: Boolean,
    val secondReview: Boolean,
    val thirdReview: Boolean
) : Parcelable
