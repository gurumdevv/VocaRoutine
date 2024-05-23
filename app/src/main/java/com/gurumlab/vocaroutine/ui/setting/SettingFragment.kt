package com.gurumlab.vocaroutine.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.databinding.FragmentSettingBinding

class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()
        setUserProfile()
    }

    private fun setLayout() {
        binding.btnSettingAccount.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingToAccountSetting()
            findNavController().navigate(action)
        }

        binding.btnSettingList.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingToListSetting()
            findNavController().navigate(action)
        }

        binding.btnPolicy.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingToPolicy()
            findNavController().navigate(action)
        }
    }

    private fun setUserProfile() {
        val user = Firebase.auth.currentUser
        user?.let {
            for (profile in it.providerData) {
                binding.ivAccountProfile.load(profile.photoUrl)
                binding.tvAccountNickName.text = profile.displayName
            }
        }
    }
}