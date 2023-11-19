package com.gurumlab.vocaroutine.ui.online

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.remote.OnlineListRepository
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
class OnlineListViewModel @Inject constructor(private val repository: OnlineListRepository) :
    ViewModel() {

    private val _sharedLists = MutableLiveData<Event<List<SharedListInfo>>>()
    val sharedList: LiveData<Event<List<SharedListInfo>>> = _sharedLists
    private val _myLists = MutableLiveData<Event<List<ListInfo>>>()
    val myLists: LiveData<Event<List<ListInfo>>> = _myLists
    private val _snackbarMessage = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarMessage

    fun loadLists() {
        viewModelScope.launch {
            val sharedLists = repository.getSharedLists()

            if (sharedLists.isNullOrEmpty()) {
                return@launch
            } else {
                _sharedLists.value = Event(sharedLists)
            }
        }
    }

    fun getMyLists() {
        viewModelScope.launch {
            val uid = repository.getUid()
            val myLists = repository.getMyLists(uid)

            if (myLists.isNullOrEmpty()) {
                return@launch
            } else {
                _myLists.value = Event(myLists)
            }
        }
    }

    fun isAlreadyCreated(response: List<ListInfo>, list: ListInfo): Boolean {
        response.forEach { myList ->
            if (myList.id == list.id) {
                _snackbarMessage.value = Event(R.string.list_already_exist)
                return true
            }
        }
        return false
    }

    fun uploadList(list: ListInfo) {
        viewModelScope.launch {
            val uid = repository.getUid()
            val date = getCurrentTime()
            val alarmCode = getAlarmCode()
            if (alarmCode == 0) {
                _snackbarMessage.value = Event(R.string.alarm_code_error_try_again)
                return@launch
            }
            val newListInfo = ListInfo(
                id = list.id,
                title = list.title,
                createdDate = date,
                totalCount = list.totalCount,
                reviewCount = 0,
                isSetAlarm = false,
                alarmCode = alarmCode,
                vocabularies = list.vocabularies
            )
            repository.uploadList(uid, newListInfo)
            _snackbarMessage.value = Event(R.string.list_download_complete)
        }
    }

    private fun getCurrentTime(): String {
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
            0
        }
    }
}