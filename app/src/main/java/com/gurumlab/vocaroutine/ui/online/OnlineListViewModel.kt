package com.gurumlab.vocaroutine.ui.online

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.remote.OnlineListRepository
import com.gurumlab.vocaroutine.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnlineListViewModel @Inject constructor(private val repository: OnlineListRepository) :
    ViewModel() {

    private val _items = MutableLiveData<Event<List<SharedListInfo>>>()
    val item: LiveData<Event<List<SharedListInfo>>> = _items

    fun loadLists() {
        viewModelScope.launch {
            val sharedLists = repository.getLists()

            if(sharedLists.isNullOrEmpty()){
                return@launch
            } else{
                _items.value = Event(sharedLists)
            }
        }
    }
}