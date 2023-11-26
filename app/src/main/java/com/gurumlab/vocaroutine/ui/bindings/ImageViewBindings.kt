package com.gurumlab.vocaroutine.ui.bindings

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.gurumlab.vocaroutine.R

@BindingAdapter("reviewStamp")
fun setStamp(view: ImageView, isReview: Boolean){
    if(isReview){
        view.setImageResource(R.drawable.ic_stamper)
    }
}