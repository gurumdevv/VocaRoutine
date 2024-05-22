package com.gurumlab.vocaroutine.data.model

data class Days(
    val first: Int,
    val second: Int,
    val third: Int
)

object InitializedDate {
    val days = Days(1, 3, 7)
}