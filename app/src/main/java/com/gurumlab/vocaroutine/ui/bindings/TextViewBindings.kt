package com.gurumlab.vocaroutine.ui.bindings

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.gurumlab.vocaroutine.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.absoluteValue

@BindingAdapter("totalCount")
fun setCount(view: TextView, count: Int) {
    view.text = view.context.getString(R.string.total_count, count)
}

@BindingAdapter("passedDays")
fun setPassedDays(view: TextView, creationDate: String) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentDate = Calendar.getInstance().time
    val createdDate = dateFormat.parse(creationDate)

    if (createdDate != null) {
        val diff = createdDate.time - currentDate.time
        val passedDays = (diff / (24 * 60 * 60 * 1000)).toInt().absoluteValue
        view.text = view.context.getString(R.string.passedDays, passedDays)
    } else {
        throw IllegalArgumentException(view.context.getString(R.string.invalid_date_format))
    }
}

@BindingAdapter("meanings")
fun setMeanings(view: TextView, meanings: String) {
    val meaningList = meanings.split(",")
    val meaning = meaningList.mapIndexed { index, value ->
        "${index + 1}. ${value.trim()}"
    }.joinToString("\n")
    view.text = meaning
}