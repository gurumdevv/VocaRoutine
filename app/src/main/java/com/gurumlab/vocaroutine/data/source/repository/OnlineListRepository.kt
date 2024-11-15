package com.gurumlab.vocaroutine.data.source.repository

import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.local.UserDataSource
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import com.gurumlab.vocaroutine.data.source.remote.onError
import com.gurumlab.vocaroutine.data.source.remote.onException
import com.gurumlab.vocaroutine.data.source.remote.onSuccess
import com.gurumlab.vocaroutine.util.FirebaseAuthenticator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class OnlineListRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val userDataSource: UserDataSource,
) {

    fun getSharedLists(
        userToken: String,
        onSuccess: () -> Unit,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, SharedListInfo>> = flow {
        val response = apiClient.getSharedList(userToken)
        response.onSuccess {
            onSuccess()
            emit(it)
        }.onError { code, message ->
            emit(emptyMap())
            onError("code: $code, message: $message")
        }.onException {
            emit(emptyMap())
            onException(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.IO)

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
            onError("code: $code, message: $message")
        }.onException {
            onException(it.message)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun uploadList(uid: String, userToken: String, listInfo: ListInfo) {
        apiClient.uploadList(uid, userToken, listInfo)
    }

    suspend fun getUid(): String {
        return userDataSource.getUid()
    }

    suspend fun getUserToken(): String {
        return FirebaseAuthenticator.getUserToken().takeIf { !it.isNullOrBlank() } ?: ""
    }
}