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
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.databinding.FragmentListSettingBinding
import com.gurumlab.vocaroutine.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListSettingFragment : BaseFragment<FragmentListSettingBinding>() {

    private val viewModel by viewModels<SettingViewModel>()
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListSettingBinding {
        return FragmentListSettingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setLayout()
        setMyListsCount()
        setSharedListsCount()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        hideBottomNavigation(true)
    }


    private fun setLayout() {
        setTopAppBar()
        setSnackbarMessage()
        hideBottomNavigation(false)
    }

    private fun setMyListsCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadMyListCount()
                viewModel.myListSize.collect { size ->
                    binding.tvCountMyList.text = getString(R.string.count_my_list, size)
                }
            }
        }
    }

    private fun setSharedListsCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadSharedListCount()
                viewModel.sharedListSize.collect { size ->
                    binding.tvCountShareList.text =
                        getString(R.string.count_share_list, size)
                }
            }
        }
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

    private fun setTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun hideBottomNavigation(visible: Boolean) {
        val bottomNavigation = activity?.findViewById<View>(R.id.bottom_navigation)
        bottomNavigation?.isVisible = visible
    }
}