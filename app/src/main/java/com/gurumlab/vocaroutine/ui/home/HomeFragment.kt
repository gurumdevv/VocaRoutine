package com.gurumlab.vocaroutine.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.databinding.FragmentHomeBinding
import com.gurumlab.vocaroutine.ui.common.EventObserver

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var viewModel: HomeViewModel

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        setLoadingLayout()
        setEmptyLayout()
        setFinishLayout()
        setSnackbar()
    }

    private fun setLoadingLayout() {
        viewModel.isLoading.observe(viewLifecycleOwner, EventObserver { isLoading ->
            if (isLoading) {
                binding!!.lottieLoading.visibility = View.VISIBLE
            } else {
                binding!!.lottieLoading.visibility = View.GONE
                binding!!.containerQuiz.visibility = View.VISIBLE
            }
        })
    }

    private fun setEmptyLayout() {
        viewModel.isEmpty.observe(viewLifecycleOwner, EventObserver { isEmpty ->
            if (isEmpty) {
                binding!!.lottieStudy.visibility = View.VISIBLE
                binding!!.containerQuiz.visibility = View.GONE
                binding!!.tvStudy.isVisible = true
                binding!!.tvStudy.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.fade_in
                    )
                )
            }
        })
    }

    private fun setFinishLayout() {
        viewModel.isFinish.observe(viewLifecycleOwner, EventObserver { isFinish ->
            if (isFinish) {
                binding!!.containerQuiz.visibility = View.GONE
                binding!!.lottieFinish.visibility = View.VISIBLE
                binding!!.tvFinish.visibility = View.VISIBLE
                binding!!.tvFinish.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.fade_in
                    )
                )
            }
        })
    }

    private fun setSnackbar() {
        viewModel.snackbarMessage.observe(viewLifecycleOwner, EventObserver { snackbarMessage ->
            Snackbar.make(requireView(), snackbarMessage, Snackbar.LENGTH_SHORT)
                .setAnchorView(R.id.bottom_navigation)
                .show()
        })
    }
}