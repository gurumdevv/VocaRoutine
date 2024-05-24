package com.gurumlab.vocaroutine.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.Manifest
import android.os.Build
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.gurumlab.vocaroutine.databinding.FragmentPermissionDialogBinding
import com.gurumlab.vocaroutine.ui.BaseDialogFragment

class PermissionDialogFragment : BaseDialogFragment<FragmentPermissionDialogBinding>() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    goToDeniedDialog()
                } else {
                    dismiss()
                }
            }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPermissionDialogBinding {
        return FragmentPermissionDialogBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAgree.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        binding.btnLater.setOnClickListener {
            goToDeniedDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().resizeDialogFragment(this, 0.6f, 0.37f)
    }

    private fun goToDeniedDialog() {
        val action =
            PermissionDialogFragmentDirections.actionPermissionDialogToPermissionDeniedDialog()
        findNavController().navigate(action)
    }
}