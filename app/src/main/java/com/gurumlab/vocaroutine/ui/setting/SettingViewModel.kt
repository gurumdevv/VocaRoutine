package com.gurumlab.vocaroutine.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.source.repository.SettingRepository
import com.gurumlab.vocaroutine.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    private val _isAccountDelete: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isAccountDelete: SharedFlow<Boolean> = _isAccountDelete

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isNetworkAvailable = MutableLiveData<Event<Boolean>>()
    val isNetworkAvailable: LiveData<Event<Boolean>> = _isNetworkAvailable

    suspend fun loadMyListCount() {
        repository.getUserToken().takeIf { it.isNotBlank() }?.let { userToken ->
            val uid = repository.getUid()
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
    }

    suspend fun loadSharedListCount() {
        repository.getUserToken().takeIf { it.isNotBlank() }?.let { userToken ->
            val uid = repository.getUid()
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
    }

    private suspend fun deleteLists(
        uid: String,
        userToken: String,
        onCompleteSharedList: () -> Unit,
        onCompleteMyList: () -> Unit,
        failMessageId: Int,
        deleteMyList: Boolean = true
    ) {
        handleDeleteSharedList(uid, userToken, failMessageId, onCompleteSharedList)

        if (deleteMyList) {
            handleDeleteMyList(uid, userToken, failMessageId, onCompleteMyList)
        }
    }

    private suspend fun handleDeleteSharedList(
        uid: String,
        userToken: String,
        failMessageId: Int,
        onComplete: () -> Unit
    ) {
        repository.deleteSharedList(
            uid,
            userToken,
            onComplete = onComplete,
            onError = { handleFailure(it, failMessageId) },
            onException = { handleFailure(it, failMessageId) }
        )
    }

    private suspend fun handleDeleteMyList(
        uid: String,
        userToken: String,
        failMessageId: Int,
        onComplete: () -> Unit
    ) {
        try {
            repository.deleteMyList(uid, userToken)
            onComplete()
        } catch (e: Exception) {
            handleFailure(e.message, failMessageId)
        }
    }

    private fun handleFailure(message: String?, failMessageId: Int) {
        if (!message.isNullOrBlank()) {
            setSnackbarMessage(failMessageId)
            crashlytics.log(message)
        }
    }

    fun deleteShareLists() {
        if (!checkNetworkAvailable(R.string.fail_delete_list)) return

        viewModelScope.launch {
            val uid = repository.getUid()
            val userToken = repository.getUserToken()
            deleteLists(
                uid,
                userToken,
                onCompleteSharedList = {
                    _sharedListSize.value = 0
                    setSnackbarMessage(R.string.delete_complete_shared_list)
                },
                onCompleteMyList = {},
                failMessageId = R.string.fail_delete_list,
                deleteMyList = false
            )
        }
    }

    fun deleteAllMyLists() {
        if (!checkNetworkAvailable(R.string.fail_delete_list)) return

        viewModelScope.launch {
            val uid = repository.getUid()
            val userToken = repository.getUserToken()
            deleteLists(
                uid,
                userToken,
                onCompleteSharedList = {
                    _sharedListSize.value = 0
                },
                onCompleteMyList = {
                    _myListSize.value = 0
                    setSnackbarMessage(R.string.delete_complete_my_list)
                },
                failMessageId = R.string.fail_delete_list
            )
        }
    }

    fun deleteAccount() {
        if (!checkNetworkAvailable(R.string.fail_delete_account)) return

        viewModelScope.launch {
            _isLoading.value = true

            val uid = repository.getUid()
            val userToken = repository.getUserToken()

            deleteLists(
                uid,
                userToken,
                onCompleteSharedList = {
                    _sharedListSize.value = 0
                },
                onCompleteMyList = {
                    _myListSize.value = 0
                },
                failMessageId = R.string.fail_delete_list
            )

            try {
                Firebase.auth.currentUser!!.delete().await()
                repository.setUid("")
                repository.deleteAllMyAlarm()
                setSnackbarMessage(R.string.success_account_delete)
                _isAccountDelete.emit(true)
            } catch (e: Exception) {
                crashlytics.log("${e.message}")
                setSnackbarMessage(R.string.fail_delete_account)
            }
            _isLoading.value = false
        }
    }

    fun logOut() {
        viewModelScope.launch {
            repository.setUid("")
            repository.deleteAllMyAlarm()
            FirebaseAuth.getInstance().signOut()
            _isLogout.emit(true)
        }
    }

    private fun checkNetworkAvailable(failMessageId: Int): Boolean {
        if (isNetworkAvailable.value?.content == false) {
            setSnackbarMessage(failMessageId)
            return false
        }
        return true
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