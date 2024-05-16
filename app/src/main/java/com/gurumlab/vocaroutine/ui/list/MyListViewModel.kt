package com.gurumlab.vocaroutine.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.source.repository.MyListRepository
import com.gurumlab.vocaroutine.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyListViewModel @Inject constructor(
    private val repository: MyListRepository,
    private val crashlytics: FirebaseCrashlytics
) : ViewModel() {

    val onlineItems: StateFlow<List<ListInfo>> = loadLists().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val offlineItems: StateFlow<List<ListInfo>?> = loadOfflineLists().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _isException: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isException: StateFlow<Boolean> = _isException

    private val _snackbarMessage: MutableSharedFlow<Int> = MutableSharedFlow()
    val snackbarMessage: SharedFlow<Int> = _snackbarMessage

    private val _isNetworkAvailable = MutableLiveData<Event<Boolean>>()
    val isNetworkAvailable: LiveData<Event<Boolean>> = _isNetworkAvailable

    private fun loadLists(): Flow<List<ListInfo>> = flow {
        val uid = repository.getUid()
        val userToken = repository.getUserToken()
        val list = repository.getLists(
            uid,
            userToken,
            onComplete = { _isLoading.value = false },
            onSuccess = {
                _isError.value = false
                _isException.value = false
            }, onError = {
                _isError.value = true
                if (!it.isNullOrBlank()) {
                    crashlytics.log(it)
                }
            }, onException = {
                _isException.value = true
                if (!it.isNullOrBlank()) {
                    crashlytics.log(it)
                }
            }
        ).map { data ->
            data.values.toList()
        }

        emitAll(list)
    }

    fun deleteList(listInfo: ListInfo) {
        viewModelScope.launch {
            val uid = repository.getUid()
            val userToken = repository.getUserToken()
            val list = repository.getListsById(
                uid,
                userToken,
                listInfo.id,
                onError = {
                    setSnackbarMessage(R.string.delete_fail)
                    if (!it.isNullOrBlank()) {
                        crashlytics.log(it)
                    }
                },
                onException = {
                    setSnackbarMessage(R.string.delete_fail)
                    if (!it.isNullOrBlank()) {
                        crashlytics.log(it)
                    }
                }
            ).firstOrNull()

            list?.let {
                val listKey = it.keys.first()
                repository.deleteList(uid, userToken, listKey)
            }
        }
    }

    private fun loadOfflineLists(): Flow<List<ListInfo>> = repository.getAllOfflineLists(
        onComplete = { _isLoading.value = false }
    )

    fun deleteOfflineList(listInfo: ListInfo) {
        viewModelScope.launch {
            repository.deleteOfflineList(listInfo)
        }
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