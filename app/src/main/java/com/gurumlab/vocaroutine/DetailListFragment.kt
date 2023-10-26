package com.gurumlab.vocaroutine

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gurumlab.vocaroutine.databinding.FragmentDetailListBinding


class DetailListFragment : BaseFragment<FragmentDetailListBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailListBinding {
        return FragmentDetailListBinding.inflate(inflater, container, false)
    }

}