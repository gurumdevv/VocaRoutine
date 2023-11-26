package com.gurumlab.vocaroutine.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.databinding.FragmentSettingBinding
import com.gurumlab.vocaroutine.ui.common.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    private val viewModel by viewModels<SettingViewModel>()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = viewLifecycleOwner

        setMyListsCount()
        setSharedListsCount()
        setSnackbarMessage()
        setLogout()
        setUserProfile()
    }

    private fun setMyListsCount() {
        viewModel.loadMyListCount()
        viewModel.myListCount.observe(viewLifecycleOwner, EventObserver { count ->
            binding!!.tvCountMyList.text = getString(R.string.count_my_list, count)
        })
    }

    private fun setSharedListsCount() {
        viewModel.loadSharedListCount()
        viewModel.sharedListCount.observe(viewLifecycleOwner, EventObserver { count ->
            binding!!.tvCountShareList.text = getString(R.string.count_share_list, count)
        })
    }

    private fun setSnackbarMessage() {
        viewModel.snackbarMessage.observe(viewLifecycleOwner, EventObserver { messageId ->
            Snackbar.make(requireView(), getString(messageId), Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.bottom_navigation)
                .show()
        })
    }

    private fun setLogout() {
        viewModel.isLogout.observe(viewLifecycleOwner, EventObserver { isCompleted ->
            if (isCompleted) {
                val action = SettingFragmentDirections.actionSettingToLogin()
                findNavController().navigate(action)
            }
        })
    }

    private fun setUserProfile() {
        val user = Firebase.auth.currentUser
        user?.let {
            for (profile in it.providerData) {
                binding!!.ivAccountProfile.load(profile.photoUrl)
                binding!!.tvAccountNickName.text = profile.displayName
            }
        }
    }
}