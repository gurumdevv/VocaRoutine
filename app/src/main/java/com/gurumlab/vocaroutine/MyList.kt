package com.gurumlab.vocaroutine

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyList(
    val name: String,
    val creationDate: String,
    val totalCount: Int,
    val reviewCount: Int,
    val alarm: Boolean,
    val vocabularies: List<Vocabulary>
) : Parcelable