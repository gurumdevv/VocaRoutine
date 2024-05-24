package com.gurumlab.vocaroutine.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gurumlab.vocaroutine.databinding.FragmentPermissionDeniedDialogBinding
import com.gurumlab.vocaroutine.ui.BaseDialogFragment

class PermissionDeniedDialogFragment : BaseDialogFragment<FragmentPermissionDeniedDialogBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPermissionDeniedDialogBinding {
        return FragmentPermissionDeniedDialogBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnOk.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().resizeDialogFragment(this, 0.75f, 0.35f)
    }
}