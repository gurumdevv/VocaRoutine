package com.gurumlab.vocaroutine.ui.bindings

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.gurumlab.vocaroutine.util.StampImageView

@BindingAdapter(value = ["app:reviewStamp", "app:day"], requireAll = true)
fun setStamp(view: ImageView, isReview: Boolean, day: Int) {
    if (view is StampImageView) {
        view.setStamp(isReview, day)
    }
}