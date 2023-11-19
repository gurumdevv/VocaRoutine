package com.gurumlab.vocaroutine.ui.making

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.TempListInfo
import com.gurumlab.vocaroutine.data.model.Vocabulary
import com.gurumlab.vocaroutine.data.source.remote.MakingListRepository
import com.gurumlab.vocaroutine.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MakingListViewModel @Inject constructor(private val repository: MakingListRepository) :
    ViewModel() {

    val word = MutableLiveData<String>()
    val meaning = MutableLiveData<String>()
    private val etymology = MutableLiveData<String>()
    private val _alarmCode = MutableLiveData<Event<Int>>()
    val alarmCode: LiveData<Event<Int>> = _alarmCode
    private val _tempList = MutableLiveData<Event<TempListInfo>>()
    val tempList: LiveData<Event<TempListInfo>> = _tempList
    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText
    private val _isCompleted = MutableLiveData<Event<Boolean>>()
    val isCompleted = _isCompleted
    private val _numberOfAttempts = MutableLiveData<Event<Int>>()
    val numberOfAttempts = _numberOfAttempts
    private val vocabularies = mutableListOf<Vocabulary>()

    suspend fun createVocabulary() {
        val currentWord = word.value ?: ""
        val currentMeaning = meaning.value ?: ""
        if (!isValidValue(currentWord, R.string.fill_in_blank_word) ||
            !isValidValue(currentMeaning, R.string.fill_in_blank_meaning)
        ) return

        _isCompleted.value = Event(false)

        etymology.value = repository.getEtymology(currentWord)

        var count = 0
        numberOfAttempts.value = Event(count)

        while (etymology.value == "error") {
            etymology.value = repository.getEtymology(currentWord)

            count++
            numberOfAttempts.value = Event(count)
            if (count >= 2) {
                break
            }
        }

        val vocabulary = Vocabulary(currentWord, currentMeaning, etymology.value!!)
        vocabularies.add(vocabulary)
        _isCompleted.value = Event(true)
    }

    fun createList() {
        viewModelScope.launch {
            createVocabulary()

            if (vocabularies.isEmpty()) {
                _snackbarText.value = Event(R.string.empty_list)
            } else {
                val id = getId()
                val uid = repository.getUid()
                val totalCount = vocabularies.size
                val date = getCurrentTime()
                val currentAlarmCode = getAlarmCode()
                _alarmCode.value = Event(currentAlarmCode)

                if (alarmCode.value!!.content == 0) {
                    _snackbarText.value = Event(R.string.alarm_code_creation_error)
                }

                _tempList.value =
                    Event(
                        TempListInfo(
                            id = id,
                            creator = uid,
                            createdDate = date,
                            totalCount = totalCount,
                            reviewCount = 0,
                            isSetAlarm = false,
                            alarmCode = currentAlarmCode,
                            vocabularies = vocabularies.toList()
                        )
                    )
            }
        }
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = Date()
        return dateFormat.format(currentTime)
    }

    private suspend fun getId(): String {
        val uid = repository.getUid()
        val alarmCode = getAlarmCode()
        return ("${uid}${alarmCode}")
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
            0
        }
    }

    fun setErrorMessage(message: String) {
        etymology.value = message
    }

    private fun isValidValue(value: String, messageId: Int): Boolean {
        if (value.isBlank()) {
            _snackbarText.value = Event(messageId)
            return false
        }
        return true
    }
}