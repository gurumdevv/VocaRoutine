package com.gurumlab.vocaroutine.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gurumlab.vocaroutine.data.model.MyList
import com.gurumlab.vocaroutine.data.source.remote.MyListRepository
import com.gurumlab.vocaroutine.ui.common.Event
import kotlinx.coroutines.launch

class MyListViewModel(private val repository: MyListRepository) : ViewModel() {

    private val _items = MutableLiveData<Event<List<MyList>>>()
    val item: LiveData<Event<List<MyList>>> = _items

    fun loadLists() {
        viewModelScope.launch {
            val myLists = repository.getLists()
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