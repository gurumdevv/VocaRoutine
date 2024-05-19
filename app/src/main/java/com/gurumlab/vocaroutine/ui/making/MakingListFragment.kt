package com.gurumlab.vocaroutine.ui.making

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.databinding.FragmentMakingListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MakingListFragment : BaseFragment<FragmentMakingListBinding>() {

    private val viewModel by viewModels<MakingListViewModel>()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMakingListBinding {
        return FragmentMakingListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setTopAppBar()
        setObservers()
        setBtnNextClickListener()
        setIvScanClickListener()
        hideBottomNavigation(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideBottomNavigation(true)
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isCompleted.collect { isCompleted ->
                        binding.etVocabulary.isEnabled = isCompleted
                        binding.etMeaning.isEnabled = isCompleted
                        binding.btnNext.isEnabled = isCompleted
                        binding.btnDone.isEnabled = isCompleted
                        binding.imgBtnCamera.isEnabled = isCompleted
                        if (isCompleted) {
                            binding.etVocabulary.setText("")
                            binding.etMeaning.setText("")
                            binding.btnNext.text = getString(R.string.next)
                        } else {
                            binding.btnNext.text = getString(R.string.loading_etymology_now)
                        }
                    }
                }

                launch {
                    viewModel.alarmCode.collect { alarmCode ->
                        if (alarmCode == 0) {
                            viewModel.setSnackbarMessage(R.string.alarm_code_creation_error)
                            findNavController().navigateUp()
                        }
                    }
                }

                launch {
                    viewModel.getUserToken().takeIf { it.isNotBlank() }?.let { userToken ->
                        viewModel.tempList.collect { tempListInfo ->
                            val action = MakingListFragmentDirections.actionCreationToDialog(
                                tempListInfo,
                                userToken
                            )
                            findNavController().navigate(action)
                        }
                    } ?: run {
                        viewModel.setSnackbarMessage(R.string.fail_authentication)
                    }
                }

                launch {
                    viewModel.snackbarMessage.collect { messageId ->
                        Snackbar.make(requireView(), getString(messageId), Snackbar.LENGTH_SHORT)
                            .setAnchorView(binding.btnDone)
                            .show()
                    }
                }
            }
        }
    }

    private fun setBtnNextClickListener() {
        binding.btnNext.setOnClickListener {
            lifecycleScope.launch {
                viewModel.createVocabulary()
            }
        }
    }

    private fun setIvScanClickListener() {
        binding.imgBtnCamera.setOnClickListener {
            val action = MakingListFragmentDirections.actionCreationToAfterPhoto()
            findNavController().navigate(action)
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