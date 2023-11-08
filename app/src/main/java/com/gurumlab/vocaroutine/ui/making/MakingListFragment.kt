package com.gurumlab.vocaroutine.ui.making

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.BuildConfig
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.VocaRoutineApplication
import com.gurumlab.vocaroutine.data.model.ChatMessage
import com.gurumlab.vocaroutine.data.model.ChatRequest
import com.gurumlab.vocaroutine.data.model.TempListInfo
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

    private val gptApiClient =
        VocaRoutineApplication.appContainer.provideGptApiClient(BuildConfig.GPT_API_KEY)
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
            lifecycleScope.launch {
                createVocabulary()
            }
        }

        binding!!.btnDone.setOnClickListener {
            lifecycleScope.launch {
                createVocabulary()

                if (vocabularies.isEmpty()) {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.empty_list),
                        Snackbar.LENGTH_SHORT
                    )
                        .setAnchorView(binding!!.btnDone)
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
                        MakingListFragmentDirections.actionCreationToDialog(uid, tempListInfo)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private suspend fun createVocabulary() {
        val word = binding!!.tvVocabulary.text ?: ""
        val meaning = binding!!.tvMeaning.text ?: ""
        var numberOfAttempts = 0

        if (word.isNotEmpty() && meaning.isNotEmpty()) {
            var etymology = getEtymology(word.toString())

            while (etymology == "error") {
                etymology = getEtymology(word.toString())

                numberOfAttempts++
                if (numberOfAttempts >= 2) {
                    etymology = getString(R.string.gpt_response_error)
                    break
                }
            }

            val vocabulary = Vocabulary(word.toString(), meaning.toString(), etymology)
            vocabularies.add(vocabulary)
            totalCount++

            binding!!.tvVocabulary.setText("")
            binding!!.tvMeaning.setText("")

            binding!!.btnNext.text = getString(R.string.next)
        } else {
            Snackbar.make(requireView(), getString(R.string.fill_in_blank), Snackbar.LENGTH_SHORT)
                .setAnchorView(binding!!.btnDone)
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
            )
                .setAnchorView(binding!!.btnDone)
                .show()
            0
        }
    }

    private suspend fun getEtymology(word: String): String {
        binding!!.btnNext.text = getString(R.string.loading_etymology_now)

        return try {
            val request = gptApiClient.getResponse(
                ChatRequest(
                    "gpt-3.5-turbo-1106", listOf(
                        ChatMessage("system", "\'${word}\'에 대한 어원만 알려줘. 30자 이내로 간단하게 설명해줘.")
                    )
                )
            )
            request.choices.first().message.content
        } catch (e: Exception) {
            getString(R.string.error)
        }
    }
}