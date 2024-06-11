package com.gurumlab.vocaroutine.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.databinding.FragmentLoginBinding
import com.gurumlab.vocaroutine.ui.BaseFragment

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()
    }

    private fun setLayout() {
        binding.btnGoogleSignIn.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginToPolicyAgreement()
            findNavController().navigate(action)
        }
        hideBottomNavigation()
    }

    private fun hideBottomNavigation() {
        val bottomNavigation = activity?.findViewById<View>(R.id.bottom_navigation)
        bottomNavigation?.isVisible = false
    }
}