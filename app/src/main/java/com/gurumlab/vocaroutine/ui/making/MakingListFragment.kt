package com.gurumlab.vocaroutine.ui.making

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.databinding.FragmentMakingListBinding
import com.gurumlab.vocaroutine.ui.common.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MakingListFragment : BaseFragment<FragmentMakingListBinding>() {

    private lateinit var uid: String

    private val viewModel by viewModels<MakingListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            uid = viewModel.getUid()
        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMakingListBinding {
        return FragmentMakingListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = viewLifecycleOwner

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
        viewModel.isCompleted.observe(viewLifecycleOwner, EventObserver { isCompleted ->
            if (isCompleted) {
                binding!!.etVocabulary.setText("")
                binding!!.etMeaning.setText("")
                binding!!.etVocabulary.isEnabled = true
                binding!!.etMeaning.isEnabled = true
                binding!!.btnNext.isEnabled = true
                binding!!.btnDone.isEnabled = true
                binding!!.ivCamera.isEnabled = true
                binding!!.btnNext.text = getString(R.string.next)
            } else {
                binding!!.etVocabulary.isEnabled = false
                binding!!.etMeaning.isEnabled = false
                binding!!.btnNext.isEnabled = false
                binding!!.btnDone.isEnabled = false
                binding!!.ivCamera.isEnabled = true
                binding!!.btnNext.text = getString(R.string.loading_etymology_now)
            }
        })

        viewModel.alarmCode.observe(viewLifecycleOwner, EventObserver { alarmCode ->
            if (alarmCode == 0) {
                findNavController().navigateUp()
            }
        })

        viewModel.tempList.observe(viewLifecycleOwner, EventObserver { tempListInfo ->
            val action =
                MakingListFragmentDirections.actionCreationToDialog(uid, tempListInfo)
            findNavController().navigate(action)
        })

        viewModel.snackbarText.observe(viewLifecycleOwner, EventObserver { messageId ->
            Snackbar.make(requireView(), getString(messageId), Snackbar.LENGTH_SHORT)
                .setAnchorView(binding!!.btnDone)
                .show()
        })
    }

    private fun setBtnNextClickListener() {
        binding!!.btnNext.setOnClickListener {
            lifecycleScope.launch {
                viewModel.createVocabulary()
            }
        }
    }

    private fun setIvScanClickListener() {
        binding!!.ivLoadPictureBackground.setOnClickListener {
            val action = MakingListFragmentDirections.actionCreationToAfterPhoto()
            findNavController().navigate(action)
        }
    }

    private fun setTopAppBar() {
        binding!!.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun hideBottomNavigation(visible: Boolean) {
        val bottomNavigation = activity?.findViewById<View>(R.id.bottom_navigation)
        bottomNavigation?.isVisible = visible
    }
}