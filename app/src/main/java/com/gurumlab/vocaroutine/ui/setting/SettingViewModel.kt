package com.gurumlab.vocaroutine.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val repository: SettingRepository,
    private val crashlytics: FirebaseCrashlytics
) :
    ViewModel() {

    val myList: StateFlow<List<ListInfo>> = loadMyList().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val sharedList: StateFlow<List<SharedListInfo>> = loadSharedListCount().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _snackbarMessage: MutableSharedFlow<Int> = MutableSharedFlow()
    val snackbarMessage: SharedFlow<Int> = _snackbarMessage

    private val _isLogout: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isLogout: SharedFlow<Boolean> = _isLogout

    private fun loadMyList(): Flow<List<ListInfo>> = flow {
        val uid = repository.getUid()
        val list = repository.getMyLists(
            uid,
            onError = {
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

        emitAll(list)
    }

    private fun loadSharedListCount(): Flow<List<SharedListInfo>> = flow {
        val uid = repository.getUid()
        val list = repository.getSharedListByCreator(
            uid,
            onError = {
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

        emitAll(list)
    }

    fun deleteAllMyLists() {
        viewModelScope.launch {
            val uid = repository.getUid()
            repository.deleteSharedList(
                uid,
                onError = {
                    if (!it.isNullOrBlank()) {
                        crashlytics.log(it)
                    }
                },
                onException = {
                    if (!it.isNullOrBlank()) {
                        crashlytics.log(it)
                    }
                }
            )
            repository.deleteMyList(uid)
            _snackbarMessage.emit(R.string.delete_complete_my_list)
        }
    }

    fun deleteShareLists() {
        viewModelScope.launch {
            val uid = repository.getUid()
            repository.deleteSharedList(
                uid,
                onError = {
                    if (!it.isNullOrBlank()) {
                        crashlytics.log(it)
                    }
                },
                onException = {
                    if (!it.isNullOrBlank()) {
                        crashlytics.log(it)
                    }
                }
            )
            _snackbarMessage.emit(R.string.delete_complete_shared_list)
        }
    }

    fun logOut() {
        viewModelScope.launch {
            repository.setUid("")
            FirebaseAuth.getInstance().signOut()
            _isLogout.emit(true)
            _snackbarMessage.emit(R.string.logout_done)
        }
    }
}