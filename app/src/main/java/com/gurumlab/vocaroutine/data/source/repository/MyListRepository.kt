package com.gurumlab.vocaroutine.data.source.repository

import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.source.local.OfflineModeDao
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

class MyListRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val userDataSource: UserDataSource,
    private val offlineModeDao: OfflineModeDao
) {

    fun getLists(
        uid: String,
        userToken: String,
        onComplete: () -> Unit,
        onSuccess: () -> Unit,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, ListInfo>> = flow {
        val response = apiClient.getLists(uid, userToken)
        response.onSuccess {
            emit(it)
            onSuccess()
        }.onError { code, message ->
            emit(emptyMap())
            onError("code: $code, message: $message")
        }.onException {
            onException(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.IO)

    suspend fun deleteList(uid: String, userToken: String, listId: String) {
        apiClient.deleteMyList(uid, listId, userToken)
    }

    fun getListsById(
        uid: String,
        userToken: String,
        listId: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, ListInfo>> = flow {
        val response = apiClient.getListsById(uid, userToken, "\"id\"", "\"${listId}\"")
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onException(it.message)
        }
    }.flowOn(Dispatchers.IO)

    fun getAllOfflineLists(
        onComplete: () -> Unit
    ): Flow<List<ListInfo>> = flow {
        emit(offlineModeDao.getAllListInfo())
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.IO)


    suspend fun deleteOfflineList(listInfo: ListInfo) {
        offlineModeDao.deleteListInfo(listInfo)
    }

    suspend fun getUid(): String {
        return userDataSource.getUid()
    }

    suspend fun getUserToken(): String {
        return FirebaseAuthenticator.getUserToken().takeIf { !it.isNullOrBlank() } ?: ""
    }
}