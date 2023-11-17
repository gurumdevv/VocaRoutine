package com.gurumlab.vocaroutine

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gurumlab.vocaroutine.databinding.FragmentSettingBinding

class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(inflater, container, false)
    }

}