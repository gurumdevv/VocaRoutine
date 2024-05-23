package com.gurumlab.vocaroutine.data.source.repository

import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.local.AlarmDao
import com.gurumlab.vocaroutine.data.source.local.UserDataSource
import com.gurumlab.vocaroutine.data.source.remote.onError
import com.gurumlab.vocaroutine.data.source.remote.onException
import com.gurumlab.vocaroutine.data.source.remote.onSuccess
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import com.gurumlab.vocaroutine.util.FirebaseAuthenticator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val dao: AlarmDao,
    private val userDataSource: UserDataSource
) {

    fun getMyLists(
        uid: String,
        userToken: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, ListInfo>> = flow {
        val response = apiClient.getLists(uid, userToken)
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            emit(emptyMap())
            onError("code: $code, message: $message")
        }.onException {
            onException(it.message)
        }
    }.flowOn(Dispatchers.Default)

    fun getSharedListByCreator(
        uid: String,
        userToken: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, SharedListInfo>> = flow {
        val response = apiClient.getSharedListByCreator(userToken, "\"creator\"", "\"${uid}\"")
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            emit(emptyMap())
            onError("code: $code, message: $message")
        }.onException {
            onException(it.message)
        }
    }.flowOn(Dispatchers.Default)

    suspend fun deleteSharedList(
        uid: String,
        userToken: String,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ) {
        val result = apiClient.getSharedListByCreator(userToken, "\"creator\"", "\"${uid}\"")
        result.onSuccess {
            it.keys.forEach { key ->
                apiClient.deleteSharedList(key, userToken)
            }
            onComplete()
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onException(it.message)
        }
    }

    suspend fun deleteMyList(uid: String, userToken: String) {
        apiClient.deleteAllMyLists(uid, userToken)
    }

    suspend fun deleteAllMyAlarm() {
        dao.deleteAllAlarms()
    }

    suspend fun getUid(): String {
        return userDataSource.getUid()
    }

    suspend fun setUid(uid: String) {
        userDataSource.setUid(uid)
    }

    suspend fun getUserToken(): String {
        return FirebaseAuthenticator.getUserToken().takeIf { !it.isNullOrBlank() } ?: ""
    }
}