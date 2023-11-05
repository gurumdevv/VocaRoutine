package com.gurumlab.vocaroutine.ui.bindings

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.gurumlab.vocaroutine.R

@BindingAdapter("dayOneStamp")
fun setFirstStamp(view: ImageView, reviewCount: Int) {
    if (reviewCount >= 1) {
        view.setImageResource(R.drawable.ic_stamper)
    }
}

@BindingAdapter("dayThreeStamp")
fun setSecondStamp(view: ImageView, reviewCount: Int) {
    if (reviewCount >= 2) {
        view.setImageResource(R.drawable.ic_stamper)
    }
}

@BindingAdapter("daySevenStamp")
fun setThirdStamp(view: ImageView, reviewCount: Int) {
    if (reviewCount >= 3) {
        view.setImageResource(R.drawable.ic_stamper)
    }
}