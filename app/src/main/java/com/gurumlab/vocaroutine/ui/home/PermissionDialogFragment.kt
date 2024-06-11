package com.gurumlab.vocaroutine.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.Manifest
import android.os.Build
import android.provider.Settings
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.view.ViewGroup
import android.app.AlarmManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.gurumlab.vocaroutine.databinding.FragmentPermissionDialogBinding
import com.gurumlab.vocaroutine.ui.BaseDialogFragment

@RequiresApi(Build.VERSION_CODES.S)
class PermissionDialogFragment : BaseDialogFragment<FragmentPermissionDialogBinding>() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestExactAlarmPermissionLauncher: ActivityResultLauncher<Intent>
    private val exactAlarmPermissionReceiver = ExactAlarmPermissionReceiver()
    private var isPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentFilter =
            IntentFilter(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED)
        requireContext().registerReceiver(exactAlarmPermissionReceiver, intentFilter)

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    goToDeniedDialog()
                } else {
                    checkAndRequestExactAlarmPermission()
                }
            }

        requestExactAlarmPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { _ ->
                if (isPermissionGranted) {
                    dismiss()
                } else {
                    goToDeniedDialog()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(exactAlarmPermissionReceiver)
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
            } else {
                checkAndRequestExactAlarmPermission()
            }
        }
        binding.btnLater.setOnClickListener {
            goToDeniedDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().resizeDialogFragment(this, 0.7f, 0.37f)
    }

    private fun goToDeniedDialog() {
        val action =
            PermissionDialogFragmentDirections.actionPermissionDialogToPermissionDeniedDialog()
        findNavController().navigate(action)
    }

    private fun checkAndRequestExactAlarmPermission() {
        if (!canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            requestExactAlarmPermissionLauncher.launch(intent)
        } else {
            dismiss()
        }
    }

    private fun canScheduleExactAlarms() =
        requireContext().getSystemService(AlarmManager::class.java).canScheduleExactAlarms()

    private inner class ExactAlarmPermissionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED) {
                if (canScheduleExactAlarms(context)) {
                    isPermissionGranted = true
                } else {
                    goToDeniedDialog()
                }
            }
        }

        private fun canScheduleExactAlarms(context: Context) =
            context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
    }
}