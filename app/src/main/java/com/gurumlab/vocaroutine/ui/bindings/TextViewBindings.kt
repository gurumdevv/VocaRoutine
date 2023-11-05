package com.gurumlab.vocaroutine.ui.bindings

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.gurumlab.vocaroutine.R

@BindingAdapter("totalCount")
fun setCount(view: TextView, count: Int) {
    view.text = view.context.getString(R.string.total_count, count)
}