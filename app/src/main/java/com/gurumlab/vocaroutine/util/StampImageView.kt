package com.gurumlab.vocaroutine.util

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

import com.gurumlab.vocaroutine.R

class StampImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    var day: Int = 0
    var reviewStamp: Boolean = false

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.StampImageView,
            0, 0
        ).apply {
            try {
                day = getInt(R.styleable.StampImageView_day, 0)
                reviewStamp = getBoolean(R.styleable.StampImageView_reviewStamp, false)
            } finally {
                recycle()
            }
        }
    }

    fun setStamp(isReview: Boolean, day: Int) {
        if (isReview) {
            setImageResource(R.drawable.ic_stamper)
        } else {
            when (day) {
                1 -> setImageResource(R.drawable.ic_circle_one)
                3 -> setImageResource(R.drawable.ic_circle_three)
                7 -> setImageResource(R.drawable.ic_circle_seven)
                else -> setImageResource(R.drawable.ic_circle_one)
            }
        }
    }
}