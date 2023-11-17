package com.gurumlab.vocaroutine.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.source.remote.MyListRepository
import com.gurumlab.vocaroutine.ui.common.Event
import kotlinx.coroutines.launch

class MyListViewModel(private val repository: MyListRepository) : ViewModel() {

    private val _items = MutableLiveData<Event<List<ListInfo>>>()
    val item: LiveData<Event<List<ListInfo>>> = _items

    fun loadLists() {
        viewModelScope.launch {
            val uid = repository.getUid()
            val myLists = repository.getLists(uid)
            _items.value = Event(myLists)
        }
    }

    companion object {
        fun provideFactory(repository: MyListRepository) = viewModelFactory {
            initializer {
                MyListViewModel(repository)
            }
        }
    }
}