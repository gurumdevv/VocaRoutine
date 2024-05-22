package com.gurumlab.vocaroutine.ui.bindings

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.gurumlab.vocaroutine.R

@BindingAdapter(value = ["app:reviewStamp", "app:day"], requireAll = true)
fun setStamp(view: ImageView, isReview: Boolean, day: Int) {
    if (isReview) {
        view.setImageResource(R.drawable.ic_stamper)
    } else {
        when (day) {
            1 -> view.setImageResource(R.drawable.ic_circle_one)
            3 -> view.setImageResource(R.drawable.ic_circle_three)
            7 -> view.setImageResource(R.drawable.ic_circle_seven)
            else -> view.setImageResource(R.drawable.ic_circle_one)
        }
    }
}