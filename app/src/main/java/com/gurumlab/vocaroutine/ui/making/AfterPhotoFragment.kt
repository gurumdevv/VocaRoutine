package com.gurumlab.vocaroutine.ui.making

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.databinding.FragmentAfterPhotoBinding
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.ui.common.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AfterPhotoFragment : BaseFragment<FragmentAfterPhotoBinding>() {

    private val viewModel by viewModels<AfterPhotoViewModel>()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAfterPhotoBinding {
        return FragmentAfterPhotoBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        startScanActivity()
        setTopAppBar()
        setObservers()
        setBtnNextClickListener()
        setCameraClickListener()
        hideBottomNavigation(false)
    }

    override fun onStart() {
        super.onStart()
        hideBottomNavigation(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideBottomNavigation(true)
    }

    private fun startScanActivity() {
        setResultLauncher()
        val intent = Intent(requireContext(), ScanActivity::class.java)
        resultLauncher.launch(intent)
    }

    private fun setResultLauncher() {
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val ocrResult = result.data?.getStringExtra(Constants.KEY_OCR_RESULT) ?: ""
                binding.etOcrResult.setText(ocrResult.ifEmpty { getString(R.string.result_empty) })
            } else {
                if (viewModel.vocabularies.isEmpty()) {
                    findNavController().navigateUp()
                } else {
                    binding.etOcrResult.setText(getString(R.string.result_empty))
                }
            }
        }
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
                        binding.btnCamera.isEnabled = isCompleted
                        if (isCompleted) {
                            binding.etVocabulary.setText("")
                            binding.etMeaning.setText("")
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
                    viewModel.tempList.collect { tempListInfo ->
                        val action =
                            AfterPhotoFragmentDirections.actionAfterPhotoToDialog(tempListInfo)
                        findNavController().navigate(action)
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

    private fun setCameraClickListener() {
        binding.btnCamera.setOnClickListener {
            val intent = Intent(requireContext(), ScanActivity::class.java)
            resultLauncher.launch(intent)
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