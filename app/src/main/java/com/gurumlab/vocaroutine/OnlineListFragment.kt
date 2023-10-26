package com.gurumlab.vocaroutine

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gurumlab.vocaroutine.databinding.FragmentOnlineListBinding

class OnlineListFragment : BaseFragment<FragmentOnlineListBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnlineListBinding {
        return FragmentOnlineListBinding.inflate(inflater, container, false)
    }

}