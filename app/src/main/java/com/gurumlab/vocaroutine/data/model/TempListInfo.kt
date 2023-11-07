package com.gurumlab.vocaroutine.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TempListInfo(
    val createdDate: String,
    val totalCount: Int,
    val reviewCount: Int,
    val isSetAlarm: Boolean,
    val alarmCode: Int,
    val vocabularies: List<Vocabulary>
) : Parcelable