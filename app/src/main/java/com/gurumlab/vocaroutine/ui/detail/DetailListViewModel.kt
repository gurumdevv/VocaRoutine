package com.gurumlab.vocaroutine.ui.detail

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gurumlab.vocaroutine.AlarmFunctions
import com.gurumlab.vocaroutine.VocaRoutineApplication
import com.gurumlab.vocaroutine.data.model.Alarm
import com.gurumlab.vocaroutine.data.model.MyList
import com.gurumlab.vocaroutine.data.source.remote.DetailListRepository
import com.gurumlab.vocaroutine.ui.common.Event
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class DetailListViewModel(
    application: VocaRoutineApplication,
    private val repository: DetailListRepository
) : AndroidViewModel(application) {

    private var _isNotificationSet: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isNotificationSet = _isNotificationSet
    private var _isNotificationSetError: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isNotificationSetError = _isNotificationSetError
    private val alarmFunctions = AlarmFunctions(application)
    private val alarmCode = 231103001 //해당 값은 서버에서 받아올 수 있도록 수정해야함(업로드 구현시 데이터 구조와 함께 수정)

    fun handleNotification(list: MyList) {

        viewModelScope.launch {
            if (isNotificationSet.value?.content == true) {
                cancelAlarm(alarmCode)
            } else {
                val content = list.name
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
        val isSet = alarmFunctions.callAlarm(date, alarmCode, content)
        if (isSet) {
            val alarm = Alarm(alarmCode, date, content)
            repository.addAlarm(alarm)
        } else {
            _isNotificationSetError.value = Event(true)
        }
        return isSet
    }

    private suspend fun cancelAlarm(alarmCode: Int) {
        val alarms = intArrayOf(alarmCode, alarmCode + 1, alarmCode + 2)
        val activeAlarms = repository.searchActiveAlarms(alarms)

        for (activeAlarmCode in activeAlarms) {
            alarmFunctions.cancelAlarm(activeAlarmCode)
            repository.deleteAlarm(activeAlarmCode)
        }

        _isNotificationSet.value = Event(false)
    }

    companion object {
        fun provideFactory(application: VocaRoutineApplication, repository: DetailListRepository) =
            viewModelFactory {
                initializer {
                    DetailListViewModel(application, repository)
                }
            }
    }
}