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

    @Inject
    lateinit var apiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tempListInfo = args.tempListInfo
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
                apiClient.uploadList(tempListInfo.creator, listInfo)
                findNavController().navigateUp()
                findNavController().navigateUp()
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