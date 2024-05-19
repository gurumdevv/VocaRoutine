package com.gurumlab.vocaroutine.ui.making

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SaveListDialogFragment : DialogFragment() {
    private var _binding: FragmentSaveListDialogBinding? = null
    private val binding get() = _binding!!
    private val args: SaveListDialogFragmentArgs by navArgs()
    private lateinit var tempListInfo: TempListInfo
    private lateinit var userToken: String

    @Inject
    lateinit var apiClient: ApiClient

    @Inject
    lateinit var crashlytics: FirebaseCrashlytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tempListInfo = args.tempListInfo
        userToken = args.userToken
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSaveListDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
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
            lifecycleScope.launch {
                try {
                    apiClient.uploadList(tempListInfo.creator, userToken, listInfo)
                    findNavController().navigateUp()
                    findNavController().navigateUp()
                } catch (e: Exception) {
                    crashlytics.log("${e.message}")
                    Snackbar.make(requireView(), R.string.fail_create_list, Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}