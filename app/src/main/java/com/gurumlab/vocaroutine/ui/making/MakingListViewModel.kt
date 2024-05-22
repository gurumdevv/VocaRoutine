package com.gurumlab.vocaroutine.ui.making

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.Review
import com.gurumlab.vocaroutine.data.model.TempListInfo
import com.gurumlab.vocaroutine.data.model.Vocabulary
import com.gurumlab.vocaroutine.data.source.repository.MakingListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MakingListViewModel @Inject constructor(
    private val repository: MakingListRepository,
    private val crashlytics: FirebaseCrashlytics
) :
    ViewModel() {

    val word = MutableStateFlow("")
    val meaning = MutableStateFlow("")

    private val _alarmCode: MutableSharedFlow<Int> = MutableSharedFlow()
    val alarmCode: SharedFlow<Int> = _alarmCode

    private val _tempList: MutableSharedFlow<TempListInfo> = MutableSharedFlow()
    val tempList: SharedFlow<TempListInfo> = _tempList

    private val _snackbarMessage: MutableSharedFlow<Int> = MutableSharedFlow()
    val snackbarMessage: SharedFlow<Int> = _snackbarMessage

    private val _isCompleted: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isCompleted: SharedFlow<Boolean> = _isCompleted

    private val _vocabularies = mutableListOf<Vocabulary>()
    val vocabularies: List<Vocabulary> = _vocabularies

    suspend fun createVocabulary() {
        val currentWord = word.value
        val currentMeaning = meaning.value
        if (!isValidValue(currentWord, R.string.fill_in_blank_word) ||
            !isValidValue(currentMeaning, R.string.fill_in_blank_meaning)
        ) return

        _isCompleted.emit(false)

        val etymologyResponse = repository.getEtymology(
            currentWord,
            onError = {
                setSnackbarMessage(R.string.fail_to_load_etymology)
                if (!it.isNullOrBlank()) {
                    crashlytics.log(it)
                }
            },
            onException = {
                setSnackbarMessage(R.string.fail_to_load_etymology)
                if (!it.isNullOrBlank()) {
                    crashlytics.log(it)
                }
            }
        ).firstOrNull()

        val etymology = etymologyResponse?.choices?.first()?.message?.content ?: ""
        val vocabulary = Vocabulary(currentWord, currentMeaning, etymology)
        _vocabularies.add(vocabulary)
        _isCompleted.emit(true)
    }

    fun createList() {
        viewModelScope.launch {
            if (word.value.isNotBlank() && meaning.value.isNotBlank()) createVocabulary()

            if (vocabularies.isEmpty()) {
                setSnackbarMessage(R.string.empty_list)
            } else {
                val uid = repository.getUid()
                val totalCount = vocabularies.size
                val date = getCurrentTime()
                val currentAlarmCode = getAlarmCode()
                val id = getId(currentAlarmCode)
                val review = Review(firstReview = false, secondReview = false, thirdReview = false)
                _alarmCode.emit(currentAlarmCode)

                _tempList.emit(
                    TempListInfo(
                        id = id,
                        creator = uid,
                        createdDate = date,
                        totalCount = totalCount,
                        isSetAlarm = false,
                        alarmCode = currentAlarmCode,
                        review = review,
                        vocabularies = vocabularies
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

    private suspend fun getId(alarmCode: Int): String {
        val uid = repository.getUid()
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

    private fun isValidValue(value: String, messageId: Int): Boolean {
        if (value.isBlank()) {
            setSnackbarMessage(messageId)
            return false
        }
        return true
    }

    fun setSnackbarMessage(messageId: Int) {
        viewModelScope.launch {
            _snackbarMessage.emit(messageId)
        }
    }
}