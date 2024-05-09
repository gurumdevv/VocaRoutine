package com.gurumlab.vocaroutine.ui.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.source.remote.onError
import com.gurumlab.vocaroutine.data.source.remote.onException
import com.gurumlab.vocaroutine.data.source.remote.onSuccess
import com.gurumlab.vocaroutine.data.source.repository.MyListRepository
import com.gurumlab.vocaroutine.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyListViewModel @Inject constructor(private val repository: MyListRepository) : ViewModel() {

    private val _items = MutableLiveData<Event<List<ListInfo>>>()
    val item: LiveData<Event<List<ListInfo>>> = _items
    private val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> = _isLoading
    private val _isCompleted = MutableLiveData<Event<Boolean>>()
    val isCompleted: LiveData<Event<Boolean>> = _isCompleted
    private val _isError = MutableLiveData<Event<Boolean>>()
    val isError: LiveData<Event<Boolean>> = _isError
    private val _isException = MutableLiveData<Event<Boolean>>()
    val isException: LiveData<Event<Boolean>> = _isException
    private val _snackbarMessage = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarMessage
    private val _isNetworkAvailable = MutableLiveData<Event<Boolean>>()
    val isNetworkAvailable: LiveData<Event<Boolean>> = _isNetworkAvailable

    fun loadLists() {
        _isLoading.value = Event(true)
        viewModelScope.launch {
            val uid = repository.getUid()
            val result = repository.getLists(uid)
            result.onSuccess {
                _isLoading.value = Event(false)
                _isCompleted.value = Event(true)
                _items.value = Event(it.values.toList())
            }.onError { code, message ->
                _isLoading.value = Event(false)
                _isError.value = Event(true)
                Log.d("MyListViewModel", "Error code: $code message: $message")
            }.onException { throwable ->
                _isLoading.value = Event(false)
                _isException.value = Event(true)
                Log.d("MyListViewModel", "Exception: $throwable")
            }
        }
    }

    fun deleteList(listInfo: ListInfo) {
        viewModelScope.launch {
            val uid = repository.getUid()
            val result = repository.getListsById(uid, listInfo.id)
            result.onSuccess {
                val listKey = it.keys.first()
                repository.deleteList(uid, listKey)
            }.onError { code, message ->
                Log.d("MyListViewModel", "Error code: $code message: $message")
            }.onException { throwable ->
                Log.d("MyListViewModel", "Exception: $throwable")
            }
        }
    }

    fun loadOfflineLists() {
        viewModelScope.launch {
            val result = repository.getAllOfflineLists()
            _items.value = Event(result)
            if (result.isEmpty()) {
                _isError.value = Event(true)
            }
        }
    }

    fun deleteOfflineList(listInfo: ListInfo) {
        viewModelScope.launch {
            repository.deleteOfflineList(listInfo)
        }
    }

    fun setSnackbarMessage(messageId: Int) {
        _snackbarMessage.value = Event(messageId)
    }

    fun setIsNetworkAvailable(isAvailable: Boolean) {
        _isNetworkAvailable.value = Event(isAvailable)
    }
}