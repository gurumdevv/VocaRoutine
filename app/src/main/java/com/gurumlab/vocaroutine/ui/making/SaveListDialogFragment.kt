package com.gurumlab.vocaroutine.ui.making

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.TempListInfo
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import com.gurumlab.vocaroutine.databinding.FragmentSaveListDialogBinding
import com.gurumlab.vocaroutine.ui.BaseDialogFragment
import com.gurumlab.vocaroutine.util.FirebaseAuthenticator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SaveListDialogFragment : BaseDialogFragment<FragmentSaveListDialogBinding>() {

    private val args: SaveListDialogFragmentArgs by navArgs()
    private lateinit var tempListInfo: TempListInfo

    @Inject
    lateinit var apiClient: ApiClient

    @Inject
    lateinit var crashlytics: FirebaseCrashlytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tempListInfo = args.tempListInfo
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSaveListDialogBinding {
        return FragmentSaveListDialogBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDone.setOnClickListener {
            val title = binding.etListTitle.text.toString()
            val listInfo = ListInfo(
                tempListInfo.id,
                title,
                tempListInfo.creator,
                tempListInfo.createdDate,
                tempListInfo.totalCount,
                tempListInfo.isSetAlarm,
                tempListInfo.alarmCode,
                tempListInfo.review,
                tempListInfo.vocabularies
            )

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val userToken =
                        FirebaseAuthenticator.getUserToken().takeIf { !it.isNullOrBlank() } ?: ""
                    apiClient.uploadList(tempListInfo.creator, userToken, listInfo)

                    val action = SaveListDialogFragmentDirections.actionDialogToMine()
                    findNavController().navigate(action)
                } catch (e: Exception) {
                    crashlytics.log("${e.message}")
                    Snackbar.make(view, R.string.fail_create_list, Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
}