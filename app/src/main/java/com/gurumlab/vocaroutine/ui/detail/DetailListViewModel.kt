package com.gurumlab.vocaroutine.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.vocaroutine.AlarmHandler
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.Alarm
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.model.onError
import com.gurumlab.vocaroutine.data.model.onException
import com.gurumlab.vocaroutine.data.model.onSuccess
import com.gurumlab.vocaroutine.data.source.remote.DetailListRepository
import com.gurumlab.vocaroutine.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DetailListViewModel @Inject constructor(
    private val repository: DetailListRepository,
    private val alarmHandler: AlarmHandler
) : ViewModel() {

    private var _isNotificationSet: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isNotificationSet = _isNotificationSet
    private var _isNotificationSetError: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isNotificationSetError = _isNotificationSetError
    private val _snackbarMessage = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarMessage
    private val _isClickAlarmIcon: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val isClickAlarmIcon: LiveData<Event<Boolean>> = _isClickAlarmIcon

    fun loadAlarm(list: ListInfo) {
        viewModelScope.launch {
            val alarmCode = list.alarmCode
            val alarms = intArrayOf(alarmCode, alarmCode + 1, alarmCode + 2)
            val activeAlarms = repository.searchActiveAlarms(alarms)
            if (activeAlarms.isNotEmpty()) {
                _isNotificationSet.value = Event(true)
            }
        }
    }

    fun handleNotification(list: ListInfo) {
        val alarmCode = list.alarmCode

        viewModelScope.launch {
            if (isNotificationSet.value?.content == true) {
                cancelAlarm(alarmCode)
            } else {
                val content = list.title
                val dayOne = getDate(1)
                val dayThree = getDate(3)
                val daySeven = getDate(7)

                _isNotificationSet.value = Event(setAlarm(alarmCode, content, dayOne))
                setAlarm(alarmCode + 1, content, dayThree)
                setAlarm(alarmCode + 2, content, daySeven)
            }
        }
    }

    private fun getDate(daysToAdd: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, daysToAdd)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return String.format(Locale.getDefault(), "%d-%02d-%02d 18:00:00", year, month, day)
    }

    private suspend fun setAlarm(alarmCode: Int, content: String, date: String): Boolean {
        val isSet = alarmHandler.callAlarm(date, alarmCode, content)
        if (isSet) {
            val alarm = Alarm(alarmCode, date, content)
            repository.addAlarm(alarm)
            _isClickAlarmIcon.value = Event(true)
        } else {
            _isNotificationSetError.value = Event(true)
        }
        return isSet
    }

    private suspend fun cancelAlarm(alarmCode: Int) {
        val alarms = intArrayOf(alarmCode, alarmCode + 1, alarmCode + 2)
        val activeAlarms = repository.searchActiveAlarms(alarms)

        for (activeAlarmCode in activeAlarms) {
            alarmHandler.cancelAlarm(activeAlarmCode)
            repository.deleteAlarm(activeAlarmCode)
        }

        _isNotificationSet.value = Event(false)
        _isClickAlarmIcon.value = Event(true)
    }

    fun shareListToOnline(list: ListInfo) {
        viewModelScope.launch {
            val uid = repository.getUid()
            if (uid != list.creator) {
                _snackbarMessage.value = Event(R.string.share_ignored)
                return@launch
            }

            val sharedListInfo = SharedListInfo(
                identifier = list.id,
                creator = uid,
                sharedDate = getCurrentDateTime(),
                listInfo = list
            )

            val result = repository.getSharedListById(list.id)
            result.onSuccess {
                val isAlreadyPost = it.values.toList().isNotEmpty()
                if (isAlreadyPost) {
                    _snackbarMessage.value = Event(R.string.already_share)
                } else {
                    repository.shareList(sharedListInfo)
                    _snackbarMessage.value = Event(R.string.share_complete)
                }
            }.onError { code, message ->
                Log.d("DetailListViewModel", "Error code: $code message: $message")
            }.onException { throwable ->
                Log.d("DetailListViewModel", "Exception: $throwable")
            }
        }
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun resetClickAlarmIcon() {
        _isClickAlarmIcon.value = Event(false)
    }
}