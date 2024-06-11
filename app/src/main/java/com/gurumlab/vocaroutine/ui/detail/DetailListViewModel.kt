package com.gurumlab.vocaroutine.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gurumlab.vocaroutine.util.AlarmHandler
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.Alarm
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.repository.DetailListRepository
import com.gurumlab.vocaroutine.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DetailListViewModel @Inject constructor(
    private val repository: DetailListRepository,
    private val alarmHandler: AlarmHandler,
    private val crashlytics: FirebaseCrashlytics
) : ViewModel() {

    private var _isNotificationSet: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isNotificationSet: StateFlow<Boolean?> = _isNotificationSet

    private val _isClickAlarmIcon: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isClickAlarmIcon: SharedFlow<Boolean> = _isClickAlarmIcon

    private val _isDownloaded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isDownloaded: StateFlow<Boolean> = _isDownloaded

    private val _snackbarMessage: MutableSharedFlow<Int> = MutableSharedFlow()
    val snackbarMessage: SharedFlow<Int> = _snackbarMessage

    private val _isNetworkAvailable = MutableLiveData<Event<Boolean>>()
    val isNetworkAvailable: LiveData<Event<Boolean>> = _isNetworkAvailable

    fun downloadList(listInfo: ListInfo) {
        viewModelScope.launch {
            if (!_isDownloaded.value) {
                repository.downloadListOnDevice(listInfo)
                _isDownloaded.value = true
                setSnackbarMessage(R.string.save_list_on_device_success)
            } else {
                repository.deleteListOnDevice(listInfo)
                _isDownloaded.value = false
                setSnackbarMessage(R.string.delete_list_on_device_success)
            }
        }
    }

    suspend fun loadIsDownloaded(list: ListInfo) {
        val result = repository.getListById(list.id)
        if (!result.isNullOrEmpty()) {
            _isDownloaded.value = true
        }
    }

    suspend fun loadAlarm(list: ListInfo) {
        val alarmCode = list.alarmCode
        val alarms = intArrayOf(alarmCode, alarmCode + 1, alarmCode + 2)
        val activeAlarms = repository.searchActiveAlarms(alarms)
        if (activeAlarms.isNotEmpty()) {
            _isNotificationSet.value = true
        }
    }

    fun handleNotification(list: ListInfo) {
        val alarmCode = list.alarmCode

        viewModelScope.launch {
            if (isNotificationSet.value == true) {
                cancelAlarm(alarmCode)
            } else {
                val id = list.id
                val content = list.title
                val dayOne = getDate(1)
                val dayThree = getDate(3)
                val daySeven = getDate(7)

                if (setAlarm(id, alarmCode, content, dayOne) &&
                    setAlarm(id, alarmCode + 1, content, dayThree) &&
                    setAlarm(id, alarmCode + 2, content, daySeven)
                ) {
                    _isNotificationSet.value = true
                } else {
                    setSnackbarMessage(R.string.set_review_notification_fail)
                }
            }
        }
    }

    private suspend fun setAlarm(
        id: String,
        alarmCode: Int,
        content: String,
        date: String
    ): Boolean {
        val isSet = alarmHandler.callAlarm(date, alarmCode, content)
        if (isSet) {
            val alarm = Alarm(alarmCode, id, date, content)
            repository.addAlarm(alarm)
            _isClickAlarmIcon.value = true
        } else {
            setSnackbarMessage(R.string.set_review_notification_fail)
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

        _isNotificationSet.value = false
        _isClickAlarmIcon.value = true
    }

    private fun getDate(daysToAdd: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, daysToAdd)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return String.format(Locale.getDefault(), "%d-%02d-%02d 18:00:00", year, month, day)
    }

    fun shareListToOnline(list: ListInfo) {
        viewModelScope.launch {
            val uid = repository.getUid()
            val userToken = repository.getUserToken()
            if (uid != list.creator) {
                setSnackbarMessage(R.string.share_ignored)
                return@launch
            }

            val sharedListInfo = SharedListInfo(
                identifier = list.id,
                creator = uid,
                sharedDate = getCurrentDateTime(),
                listInfo = list
            )

            val sharedList = repository.getSharedListById(
                userToken,
                list.id,
                onError = {
                    setSnackbarMessage(R.string.share_fail)
                    if (!it.isNullOrBlank()) {
                        crashlytics.log(it)
                    }
                },
                onException = {
                    setSnackbarMessage(R.string.share_fail)
                    if (!it.isNullOrBlank()) {
                        crashlytics.log(it)
                    }
                }
            ).map { data ->
                data.values.toList()
            }.firstOrNull()

            sharedList?.let {
                val isAlreadyPost = it.isNotEmpty()
                if (isAlreadyPost) {
                    setSnackbarMessage(R.string.already_share)
                } else {
                    try {
                        repository.shareList(userToken, sharedListInfo)
                        setSnackbarMessage(R.string.share_complete)
                    } catch (e: Exception) {
                        setSnackbarMessage(R.string.share_fail)
                        crashlytics.log("${e.message}")
                    }
                }
            }
        }
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun resetClickAlarmIcon() {
        _isClickAlarmIcon.value = false
    }

    fun setSnackbarMessage(messageId: Int) {
        viewModelScope.launch {
            _snackbarMessage.emit(messageId)
        }
    }

    fun setIsNetworkAvailable(isAvailable: Boolean) {
        _isNetworkAvailable.value = Event(isAvailable)
    }
}