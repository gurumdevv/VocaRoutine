package com.gurumlab.vocaroutine

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gurumlab.vocaroutine.databinding.FragmentMakingListBinding

class MakingListFragment : BaseFragment<FragmentMakingListBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMakingListBinding {
        return FragmentMakingListBinding.inflate(inflater, container, false)
    }


}