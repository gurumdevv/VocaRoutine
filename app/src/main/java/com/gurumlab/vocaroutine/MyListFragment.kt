package com.gurumlab.vocaroutine

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gurumlab.vocaroutine.databinding.FragmentMyListBinding


class MyListFragment : BaseFragment<FragmentMyListBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyListBinding {
        return FragmentMyListBinding.inflate(inflater, container, false)
    }

}