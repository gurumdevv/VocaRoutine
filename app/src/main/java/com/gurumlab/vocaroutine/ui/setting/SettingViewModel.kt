package com.gurumlab.vocaroutine.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.source.remote.SettingRepository
import com.gurumlab.vocaroutine.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val repository: SettingRepository) :
    ViewModel() {

    private val _myListCount = MutableLiveData<Event<Int>>()
    val myListCount: LiveData<Event<Int>> = _myListCount
    private val _sharedListCount = MutableLiveData<Event<Int>>()
    val sharedListCount: LiveData<Event<Int>> = _sharedListCount
    private val _snackbarMessage = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarMessage
    private val _isLogout = MutableLiveData<Event<Boolean>>()
    val isLogout: LiveData<Event<Boolean>> = _isLogout

    fun loadMyListCount() {
        viewModelScope.launch {
            val uid = repository.getUid()
            val myLists = repository.getMyLists(uid)
            if (myLists.isNullOrEmpty()) {
                _myListCount.value = Event(0)
            } else {
                _myListCount.value = Event(myLists.size)
            }
        }
    }

    fun loadSharedListCount() {
        viewModelScope.launch {
            val uid = repository.getUid()
            val shardList = repository.getSharedListByCreator(uid)
            _sharedListCount.value = Event(shardList.size)
        }
    }

    fun deleteAllMyLists() {
        viewModelScope.launch {
            val uid = repository.getUid()
            repository.deleteSharedList(uid)
            repository.deleteMyList(uid)
        }
        _snackbarMessage.value = Event(R.string.delete_complete_my_list)
    }

    fun deleteShareLists() {
        viewModelScope.launch {
            val uid = repository.getUid()
            repository.deleteSharedList(uid)
        }
        _snackbarMessage.value = Event(R.string.delete_complete_shared_list)
    }

    fun logOut() {
        viewModelScope.launch {
            repository.setUid("")
            FirebaseAuth.getInstance().signOut()
            _isLogout.value = Event(true)
            _snackbarMessage.value = Event(R.string.logout_done)
        }
    }
}