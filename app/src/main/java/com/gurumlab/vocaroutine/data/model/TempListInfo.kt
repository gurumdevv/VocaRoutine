package com.gurumlab.vocaroutine.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TempListInfo(
    val id: String,
    val creator: String,
    val createdDate: String,
    val totalCount: Int,
    val isSetAlarm: Boolean,
    val alarmCode: Int,
    val review: Review,
    val vocabularies: List<Vocabulary>
) : Parcelable