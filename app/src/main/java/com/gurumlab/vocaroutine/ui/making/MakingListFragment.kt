package com.gurumlab.vocaroutine.ui.making

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.databinding.FragmentMakingListBinding

class MakingListFragment : BaseFragment<FragmentMakingListBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMakingListBinding {
        return FragmentMakingListBinding.inflate(inflater, container, false)
    }


}