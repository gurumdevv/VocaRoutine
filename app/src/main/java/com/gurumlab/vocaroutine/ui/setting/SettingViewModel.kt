package com.gurumlab.vocaroutine.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.source.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val repository: SettingRepository,
    private val crashlytics: FirebaseCrashlytics
) :
    ViewModel() {

    private val _myListSize: MutableStateFlow<Int> = MutableStateFlow(0)
    val myListSize: StateFlow<Int> = _myListSize

    private val _sharedListSize: MutableStateFlow<Int> = MutableStateFlow(0)
    val sharedListSize: StateFlow<Int> = _sharedListSize

    private val _snackbarMessage: MutableSharedFlow<Int> = MutableSharedFlow()
    val snackbarMessage: SharedFlow<Int> = _snackbarMessage

    private val _isLogout: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isLogout: SharedFlow<Boolean> = _isLogout

    suspend fun loadMyListCount() {
        val uid = repository.getUid()
        val userToken = repository.getUserToken()
        val result = repository.getMyLists(
            uid,
            userToken,
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
            if (data.isEmpty()) emptyList()
            else data.values.toList()
        }.firstOrNull()

        result?.let { data ->
            _myListSize.value = data.size
        }
    }

    suspend fun loadSharedListCount() {
        val uid = repository.getUid()
        val userToken = repository.getUserToken()
        val result = repository.getSharedListByCreator(
            uid,
            userToken,
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
            if (data.isEmpty()) emptyList()
            else data.values.toList()
        }.firstOrNull()

        result?.let { data ->
            _sharedListSize.value = data.size
        }
    }

    fun deleteAllMyLists() {
        viewModelScope.launch {
            val uid = repository.getUid()
            val userToken = repository.getUserToken()
            repository.deleteSharedList(
                uid,
                userToken,
                onComplete = {
                    _myListSize.value = 0
                    _sharedListSize.value = 0
                },
                onError = {
                    if (!it.isNullOrBlank()) {
                        crashlytics.log(it)
                        setSnackbarMessage(R.string.fail_delete_list)
                    }
                },
                onException = {
                    if (!it.isNullOrBlank()) {
                        crashlytics.log(it)
                        setSnackbarMessage(R.string.fail_delete_list)
                    }
                }
            )

            try {
                repository.deleteMyList(uid, userToken)
                setSnackbarMessage(R.string.delete_complete_my_list)
            } catch (e: Exception) {
                crashlytics.log("${e.message}")
                setSnackbarMessage(R.string.fail_delete_list)
            }
        }
    }

    fun deleteShareLists() {
        viewModelScope.launch {
            val uid = repository.getUid()
            val userToken = repository.getUserToken()
            repository.deleteSharedList(
                uid,
                userToken,
                onComplete = {
                    setSnackbarMessage(R.string.delete_complete_shared_list)
                    _sharedListSize.value = 0
                },
                onError = {
                    if (!it.isNullOrBlank()) {
                        setSnackbarMessage(R.string.fail_delete_list)
                        crashlytics.log(it)
                    }
                },
                onException = {
                    if (!it.isNullOrBlank()) {
                        setSnackbarMessage(R.string.fail_delete_list)
                        crashlytics.log(it)
                    }
                }
            )
        }
    }

    fun logOut() {
        viewModelScope.launch {
            repository.setUid("")
            repository.setUserToken("")
            FirebaseAuth.getInstance().signOut()
            _isLogout.emit(true)
            setSnackbarMessage(R.string.logout_done)
        }
    }

    fun setSnackbarMessage(messageId: Int) {
        viewModelScope.launch {
            _snackbarMessage.emit(messageId)
        }
    }
}