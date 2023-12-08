package com.gurumlab.vocaroutine.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class ListInfo(
    @PrimaryKey val id: String,
    val title: String,
    val creator: String,
    val createdDate: String,
    val totalCount: Int,
    val isSetAlarm: Boolean,
    val alarmCode: Int,
    val review: Review,
    val vocabularies: List<Vocabulary>
) : Parcelable