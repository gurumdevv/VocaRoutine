package com.gurumlab.vocaroutine.ui.online

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.Review
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.repository.OnlineListRepository
import com.gurumlab.vocaroutine.util.AlarmCodeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OnlineListViewModel @Inject constructor(
    private val repository: OnlineListRepository,
    private val crashlytics: FirebaseCrashlytics,
    private val alarmCodeManager: AlarmCodeManager
) :
    ViewModel() {

    val sharedList: StateFlow<List<SharedListInfo>> = loadLists().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _isException: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isException: StateFlow<Boolean> = _isException

    private val _isEmptyList: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isEmptyList: StateFlow<Boolean> = _isEmptyList

    private val _snackbarMessage: MutableSharedFlow<Int> = MutableSharedFlow()
    val snackbarMessage: SharedFlow<Int> = _snackbarMessage

    private fun loadLists(): Flow<List<SharedListInfo>> = flow {
        val userToken = repository.getUserToken()
        val list = repository.getSharedLists(
            userToken,
            onComplete = { _isLoading.value = false },
            onSuccess = {
                _isError.value = false
                _isException.value = false
            },
            onError = {
                _isError.value = true
                _isException.value = false
                if (!it.isNullOrBlank()) {
                    crashlytics.log(it)
                }
            },
            onException = {
                _isError.value = false
                _isException.value = true
                if (!it.isNullOrBlank()) {
                    crashlytics.log(it)
                }
            }
        ).map { data ->
            if (data.isEmpty()) emptyList()
            else data.values.toList()
        }

        emitAll(list)
    }

    suspend fun getMyLists(): Flow<List<ListInfo>> = repository.getMyLists(
        repository.getUid(),
        repository.getUserToken(),
        onError = {
            _isEmptyList.value = true
            if (!it.isNullOrBlank()) {
                crashlytics.log(it)
            }
        },
        onException = {
            if (!it.isNullOrBlank()) {
                crashlytics.log(it)
            }
        }
    ).map { data ->
        data.values.toList()
    }

    suspend fun isAlreadyCreated(response: List<ListInfo>, list: ListInfo): Boolean {
        response.forEach { myList ->
            if (myList.id == list.id) {
                _snackbarMessage.emit(R.string.list_already_exist)
                return true
            }
        }
        return false
    }

    fun uploadToMyList(list: ListInfo) {
        viewModelScope.launch {
            val uid = repository.getUid()
            val userToken = repository.getUserToken()
            val date = getCurrentTime()
            val review = Review(firstReview = false, secondReview = false, thirdReview = false)
            val alarmCode = alarmCodeManager.getAlarmCode()
            val newListInfo = ListInfo(
                id = list.id,
                title = list.title,
                creator = list.creator,
                createdDate = date,
                totalCount = list.totalCount,
                isSetAlarm = false,
                alarmCode = alarmCode,
                review = review,
                vocabularies = list.vocabularies
            )
            try {
                repository.uploadList(uid, userToken, newListInfo)
                _snackbarMessage.emit(R.string.list_download_complete)
            } catch (e: Exception) {
                crashlytics.log("${e.message}")
                _snackbarMessage.emit(R.string.fail_download_list)
            }
        }
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = Date()
        return dateFormat.format(currentTime)
    }
}