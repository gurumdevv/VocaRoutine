package com.gurumlab.vocaroutine.data.source.repository

import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.remote.onError
import com.gurumlab.vocaroutine.data.source.remote.onException
import com.gurumlab.vocaroutine.data.source.remote.onSuccess
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val userDataSource: UserDataSource
) {

    fun getMyLists(
        uid: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, ListInfo>> = flow {
        val response = apiClient.getLists(uid)
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onException(it.message)
        }
    }.flowOn(Dispatchers.Default)

    fun getSharedListByCreator(
        uid: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, SharedListInfo>> = flow {
        val response = apiClient.getSharedListByCreator("\"creator\"", "\"${uid}\"")
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onException(it.message)
        }
    }.flowOn(Dispatchers.Default)

    suspend fun deleteSharedList(
        uid: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ) {
        val result = apiClient.getSharedListByCreator("\"creator\"", "\"${uid}\"")
        result.onSuccess {
            it.keys.forEach { key ->
                apiClient.deleteSharedList(key)
            }
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onException(it.message)
        }
    }

    suspend fun deleteMyList(uid: String) {
        apiClient.deleteAllMyLists(uid)
    }

    suspend fun getUid(): String {
        return userDataSource.getUid()
    }

    suspend fun setUid(uid: String) {
        userDataSource.setUid(uid)
    }
}