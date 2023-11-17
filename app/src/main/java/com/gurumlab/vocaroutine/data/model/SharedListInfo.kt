package com.gurumlab.vocaroutine.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SharedListInfo(
    val identifier: String,
    val creator: String,
    val sharedDate: String,
    val listInfo: ListInfo
) : Parcelable
