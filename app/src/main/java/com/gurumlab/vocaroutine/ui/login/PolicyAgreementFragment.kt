package com.gurumlab.vocaroutine.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.databinding.FragmentPolicyAgreementBinding
import com.gurumlab.vocaroutine.ui.BaseFragment

private const val TERMS_URL = "https://gurumlab.notion.site/6e5784014caf448fb27ba062664791b6?pvs=4"
private const val PRIVACY_URL =
    "https://gurumlab.notion.site/Privacy-Policy-5c7fba38926549f3ad908e8a7d4c5885?pvs=4"

class PolicyAgreementFragment : BaseFragment<FragmentPolicyAgreementBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPolicyAgreementBinding {
        return FragmentPolicyAgreementBinding.inflate(inflater, container, false)
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
        setCheckBoxClickListener()
        hideBottomNavigation(false)
    }

    private fun setCheckBoxClickListener() {
        binding.cbAllAgree.setOnClickListener {
            val isChecked = binding.cbAllAgree.isChecked
            binding.cbAge.isChecked = isChecked
            binding.cbPrivacy.isChecked = isChecked
            binding.cbTerms.isChecked = isChecked
            binding.btnAgree.isEnabled = isChecked
        }

        val checkBoxListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            updateAgreeButtonState()
        }

        binding.cbAge.setOnCheckedChangeListener(checkBoxListener)
        binding.cbPrivacy.setOnCheckedChangeListener(checkBoxListener)
        binding.cbTerms.setOnCheckedChangeListener(checkBoxListener)
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

    private fun updateAgreeButtonState() {
        val allChecked =
            binding.cbAge.isChecked && binding.cbPrivacy.isChecked && binding.cbTerms.isChecked
        binding.cbAllAgree.isChecked = allChecked
        binding.btnAgree.isEnabled = allChecked
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
