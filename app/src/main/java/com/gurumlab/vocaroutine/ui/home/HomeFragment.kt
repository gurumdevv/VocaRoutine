package com.gurumlab.vocaroutine.ui.home

import android.Manifest
import android.app.AlarmManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val viewModel by activityViewModels<HomeViewModel>()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        verifySignInUidAndCheckPermission()
        setLoadingLayout()
        setReviewLayout()
        setEmptyLayout()
        setFinishLayout()
        setSnackbar()
    }

    private fun setReviewLayout() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadList()
                launch {
                    viewModel.reviewList.collect { reviewList ->
                        if (reviewList.isNotEmpty()) {
                            binding.containerQuiz.isVisible = true
                        }
                    }
                }
            }
        }
    }

    private fun setLoadingLayout() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isLoading ->
                    binding.lottieLoading.isVisible = isLoading
                }
        }
    }

    private fun setEmptyLayout() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isEmpty
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isEmpty ->
                    binding.lottieStudy.isVisible = isEmpty
                    binding.tvStudy.isVisible = isEmpty
                    binding.tvStudy.startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(),
                            R.anim.fade_in
                        )
                    )
                }
        }
    }

    private fun setFinishLayout() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFinish
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isFinish ->
                    binding.containerQuiz.isVisible = !isFinish
                    binding.lottieFinish.isVisible = isFinish
                    binding.tvFinish.isVisible = isFinish
                    binding.tvFinish.startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(),
                            R.anim.fade_in
                        )
                    )
                }
        }
    }

    private fun setSnackbar() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.snackbarMessage
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { snackbarMessage ->
                    Snackbar.make(requireView(), snackbarMessage, Snackbar.LENGTH_SHORT)
                        .setAnchorView(R.id.bottom_navigation)
                        .show()
                }
        }
    }

    private fun verifySignInUidAndCheckPermission() {
        viewLifecycleOwner.lifecycleScope.launch {
            val isExistUid = viewModel.checkExistUid()
            if (!isExistUid) {
                val action = HomeFragmentDirections.actionHomeToLogin()
                findNavController().navigate(action)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    checkPermissions()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkPermissions() {
        lifecycleScope.launch {
            if (!viewModel.getIsPermissionCheck()) {
                val isNotNotificationPermissionGranted =
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED &&
                            !ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                Manifest.permission.POST_NOTIFICATIONS
                            )

                val isExactAlarmPermissionGranted =
                    requireContext().getSystemService(AlarmManager::class.java)
                        .canScheduleExactAlarms()

                if (isNotNotificationPermissionGranted || !isExactAlarmPermissionGranted) {
                    viewModel.setPermissionCheck(true)
                    findNavController().navigate(HomeFragmentDirections.actionHomeToPermissionDialog())
                }
            }
        }
    }
}