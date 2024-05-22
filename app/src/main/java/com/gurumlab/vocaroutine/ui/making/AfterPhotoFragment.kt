package com.gurumlab.vocaroutine.ui.making

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.databinding.FragmentAfterPhotoBinding
import com.gurumlab.vocaroutine.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class AfterPhotoFragment : BaseFragment<FragmentAfterPhotoBinding>() {

    private val viewModel by viewModels<AfterPhotoViewModel>()
    private val scannerOptions = setScannerOption()
    private val textRecognizer = setTextRecognition()
    private val scanner = setScanner()
    private val scannerLauncher = setScannerLauncher()

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

        startScan()
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
                        binding.etOcrResult.isEnabled = isCompleted
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

    private fun setScannerOption() = GmsDocumentScannerOptions.Builder()
        .setGalleryImportAllowed(false)
        .setPageLimit(1)
        .setResultFormats(RESULT_FORMAT_JPEG)
        .setScannerMode(SCANNER_MODE_FULL)
        .build()

    private fun setScanner() = GmsDocumentScanning.getClient(scannerOptions)

    private fun setTextRecognition() =
        TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    private fun setScannerLauncher() =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            binding.layoutAfterPhoto.isVisible = true
            if (result.resultCode == RESULT_OK) {
                processScanResult(result.data)
            }
        }

    private fun startScan() {
        scanner.getStartScanIntent(requireActivity())
            .addOnSuccessListener { intentSender ->
                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener {
                val snackbar = Snackbar.make(
                    requireView(),
                    getString(R.string.fail_scan_document),
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(activity?.findViewById(R.id.bottom_navigation))
                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        findNavController().navigateUp()
                    }
                })
                snackbar.show()
            }
    }

    private fun processScanResult(data: Intent?) {
        val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(data)
        scanResult?.pages?.let { pages ->
            for (page in pages) {
                val imageUri = page.imageUri
                try {
                    val image = InputImage.fromFilePath(requireContext(), imageUri)
                    recognizeTextFromImage(image)
                } catch (e: IOException) {
                    viewModel.setSnackbarMessage(R.string.please_scan_again)
                }
            }
        }
    }

    private fun recognizeTextFromImage(image: InputImage) {
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                val resultText = visionText.text.ifEmpty { getString(R.string.result_empty) }
                binding.etOcrResult.setText(resultText)
            }
            .addOnFailureListener {
                viewModel.setSnackbarMessage(R.string.please_scan_again)
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
            startScan()
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