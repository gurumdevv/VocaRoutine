package com.gurumlab.vocaroutine.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.databinding.FragmentPolicyBinding
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.ui.common.Constants.PRIVACY_URL
import com.gurumlab.vocaroutine.ui.common.Constants.TERMS_URL

class PolicyFragment : BaseFragment<FragmentPolicyBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPolicyBinding {
        return FragmentPolicyBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideBottomNavigation(true)
    }

    private fun setLayout() {
        setTopAppBar()
        setButtonClickListener()
        hideBottomNavigation(false)
    }

    private fun setButtonClickListener() {
        binding.btnTerms.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_URL))
            startActivity(intent)
        }

        binding.btnPrivacy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL))
            startActivity(intent)
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