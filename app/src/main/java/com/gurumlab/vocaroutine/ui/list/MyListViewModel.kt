package com.gurumlab.vocaroutine.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.vocaroutine.data.model.ListInfo
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
            val myLists = repository.getLists(uid)

            if(myLists.isNullOrEmpty()){
                return@launch
            } else{
                _items.value = Event(myLists)
            }
        }
    }
}