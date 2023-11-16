package com.gurumlab.vocaroutine.ui.making

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.TempListInfo
import com.gurumlab.vocaroutine.data.model.Vocabulary
import com.gurumlab.vocaroutine.data.source.local.DataStoreModule
import com.gurumlab.vocaroutine.databinding.FragmentAfterPhotoBinding
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.ui.common.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AfterPhotoFragment : BaseFragment<FragmentAfterPhotoBinding>() {

    @Inject
    lateinit var dataStore: DataStoreModule
    private lateinit var uid: String
    private val vocabularies = mutableListOf<Vocabulary>()
    private var totalCount = 0
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            uid = dataStore.getUid.first()
        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAfterPhotoBinding {
        return FragmentAfterPhotoBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setResultLauncher()
        val intent = Intent(requireContext(), ScanActivity::class.java)
        resultLauncher.launch(intent)

        binding!!.topAppBar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding!!.btnNext.setOnClickListener {
            createVocabulary()
        }

        binding!!.btnDone.setOnClickListener {
            createVocabulary()

            if (vocabularies.isEmpty()) {
                Snackbar.make(requireView(), getString(R.string.empty_list), Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                val date = getCurrentTimeAsString()
                val alarmCode = getAlarmCode()
                if (alarmCode == 0) {
                    findNavController().navigateUp()
                }

                val tempListInfo =
                    TempListInfo(date, totalCount, 0, false, alarmCode, vocabularies.toList())

                val action =
                    AfterPhotoFragmentDirections.actionAfterPhotoToDialog(uid, tempListInfo)
                findNavController().navigate(action)
            }
        }

        hideBottomNavigation(false)
    }

    override fun onStart() {
        super.onStart()
        hideBottomNavigation(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideBottomNavigation(true)
    }

    private fun createVocabulary() {
        val word = binding!!.tvVocabulary.text ?: ""
        val meaning = binding!!.tvMeaning.text ?: ""

        if (word.isNotEmpty() && meaning.isNotEmpty()) {
            val vocabulary = Vocabulary(word.toString(), meaning.toString(), "etymology")
            vocabularies.add(vocabulary)
            totalCount++

            binding!!.tvVocabulary.setText("")
            binding!!.tvMeaning.setText("")
        } else {
            Snackbar.make(
                requireView(),
                getString(R.string.fill_in_blank_word),
                Snackbar.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun getCurrentTimeAsString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = Date()
        return dateFormat.format(currentTime)
    }

    private fun getAlarmCode(): Int {
        val calendar = Calendar.getInstance()
        val currentTIme = Date()
        calendar.time = currentTIme

        val year = calendar.get(Calendar.YEAR).toString().takeLast(2).toInt()
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return try {
            "${year * month * day}${hour}${minute}0".toInt()
        } catch (e: NumberFormatException) {
            Snackbar.make(
                binding!!.root.rootView,
                getString(R.string.alarm_code_creation_error, e), Snackbar.LENGTH_LONG
            ).show()
            0
        }
    }

    private fun setResultLauncher() {
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {

                val ocrResult = result.data?.getStringExtra(Constants.KEY_OCR_RESULT) ?: ""
                binding!!.etOcrResult.setText(ocrResult)
            }
        }
    }

    private fun hideBottomNavigation(visible: Boolean) {
        val bottomNavigation = activity?.findViewById<View>(R.id.bottom_navigation)
        bottomNavigation?.isVisible = visible
    }
}