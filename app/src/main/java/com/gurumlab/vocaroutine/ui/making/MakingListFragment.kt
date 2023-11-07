package com.gurumlab.vocaroutine.ui.making

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.VocaRoutineApplication
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.Vocabulary
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.databinding.FragmentMakingListBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MakingListFragment : BaseFragment<FragmentMakingListBinding>() {

    private lateinit var uid: String
    private val vocabularies = mutableListOf<Vocabulary>()
    private var totalCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            uid = VocaRoutineApplication.getInstance().getDataStore().savedUid.first()
        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMakingListBinding {
        return FragmentMakingListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.btnNext.setOnClickListener {
            createVocabulary()
        }

        binding!!.btnDone.setOnClickListener {
            createVocabulary()
            val title = "tempTitle"
            val date = getCurrentTimeAsString()
            val alarmCode = getAlarmCode()
            if (alarmCode == 0) {
                findNavController().navigateUp()
            }

            val listInfo =
                ListInfo(title, date, totalCount, 0, false, alarmCode, vocabularies.toList())

            lifecycleScope.launch {
                VocaRoutineApplication.appContainer.provideApiClient().uploadList(uid, listInfo)
                findNavController().navigateUp()
            }
        }
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
            Snackbar.make(requireView(), getString(R.string.fill_in_blank), Snackbar.LENGTH_SHORT)
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
                requireView(),
                getString(R.string.alarm_code_creation_error, e), Snackbar.LENGTH_LONG
            ).show()
            0
        }
    }
}