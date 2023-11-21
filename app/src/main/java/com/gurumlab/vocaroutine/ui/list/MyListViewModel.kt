package com.gurumlab.vocaroutine.ui.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.onError
import com.gurumlab.vocaroutine.data.model.onException
import com.gurumlab.vocaroutine.data.model.onSuccess
import com.gurumlab.vocaroutine.data.source.remote.MyListRepository
import com.gurumlab.vocaroutine.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyListViewModel @Inject constructor(private val repository: MyListRepository) : ViewModel() {

    private val _items = MutableLiveData<Event<List<ListInfo>>>()
    val item: LiveData<Event<List<ListInfo>>> = _items

    fun loadLists() {
        viewModelScope.launch {
            val uid = repository.getUid()
            val result = repository.getLists(uid)
            result.onSuccess {
                _items.value = Event(it.values.toList())
            }.onError { code, message ->
                Log.d("MyListViewModel", "Error code: $code message: $message")
            }.onException { throwable ->
                Log.d("MyListViewModel", "Exception: $throwable")
            }
        }
    }
}