package com.gurumlab.vocaroutine.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.databinding.FragmentAlertSettingBinding
import com.gurumlab.vocaroutine.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class AlertSettingFragment : BaseFragment<FragmentAlertSettingBinding>() {

    private val viewModel by viewModels<SettingViewModel>()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAlertSettingBinding {
        return FragmentAlertSettingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setLayout()
    }

    private fun setLayout() {
        setTimePicker()
        setTopAppBar()
        showBottomNavigation(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigation(true)
    }

    private fun setTimePicker() {
        val amPm = arrayOf(getString(R.string.am), getString(R.string.pm))
        binding.numberPickerAmPm.apply {
            displayedValues = amPm
            minValue = 0
            maxValue = 1
            wrapSelectorWheel = true
        }

        binding.numberPickerHour.apply {
            minValue = 1
            maxValue = 12
            wrapSelectorWheel = true
            setFormatter { it.toTwoDigitString() }
        }

        binding.numberPickerMinute.apply {
            minValue = 0
            maxValue = 59
            wrapSelectorWheel = true
            setFormatter { it.toTwoDigitString() }
        }
    }

    private fun setTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun Int.toTwoDigitString(): String {
        return String.format(Locale.getDefault(), "%02d", this)
    }

    private fun showBottomNavigation(visible: Boolean) {
        val bottomNavigation = activity?.findViewById<View>(R.id.bottom_navigation)
        bottomNavigation?.isVisible = visible
    }
}