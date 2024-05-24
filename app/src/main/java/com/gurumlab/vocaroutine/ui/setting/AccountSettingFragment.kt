package com.gurumlab.vocaroutine.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.util.NetworkConnection
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.databinding.FragmentAccountSettingBinding
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.ui.common.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountSettingFragment : BaseFragment<FragmentAccountSettingBinding>() {

    private val viewModel by viewModels<SettingViewModel>()
    private var isGotoLogin = false
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSettingBinding {
        return FragmentAccountSettingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setLayout()
        setLogout()
        setAccountDelete()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!isGotoLogin) {
            hideBottomNavigation(true)
        }
    }

    private fun setLayout() {
        setTopAppBar()
        setLoadingView()
        setSnackbarMessage()
        hideBottomNavigation(false)
        setNetworkConnectionObserver()
    }

    private fun setLogout() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLogout
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isCompleted ->
                    if (isCompleted) {
                        isGotoLogin = true
                        val action = AccountSettingFragmentDirections.actionAccountSettingToLogin()
                        findNavController().navigate(action)
                    }
                }
        }
    }

    private fun setAccountDelete() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isAccountDelete
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isCompleted ->
                    if (isCompleted) {
                        isGotoLogin = true
                        val action = AccountSettingFragmentDirections.actionAccountSettingToLogin()
                        findNavController().navigate(action)
                    }
                }
        }
    }

    private fun setLoadingView() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isLoading ->
                    binding.lottie.isVisible = isLoading
                }
        }
    }

    private fun setNetworkConnectionObserver() {
        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner, EventObserver { isAvailable ->
            viewModel.setIsNetworkAvailable(isAvailable)
        })
    }

    private fun setTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun hideBottomNavigation(visible: Boolean) {
        val bottomNavigation = activity?.findViewById<View>(R.id.bottom_navigation)
        bottomNavigation?.isVisible = visible
    }

    private fun setSnackbarMessage() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.snackbarMessage
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { messageId ->
                    Snackbar.make(requireView(), getString(messageId), Snackbar.LENGTH_LONG)
                        .show()
                }
        }
    }
}