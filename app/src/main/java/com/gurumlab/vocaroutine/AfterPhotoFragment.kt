package com.gurumlab.vocaroutine

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gurumlab.vocaroutine.databinding.FragmentAfterPhotoBinding


class AfterPhotoFragment : BaseFragment<FragmentAfterPhotoBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAfterPhotoBinding {
        return FragmentAfterPhotoBinding.inflate(inflater, container, false)
    }

}