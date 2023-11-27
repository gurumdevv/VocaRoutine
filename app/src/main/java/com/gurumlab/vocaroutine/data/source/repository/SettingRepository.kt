package com.gurumlab.vocaroutine.data.source.repository

import android.util.Log
import com.gurumlab.vocaroutine.data.source.remote.ApiResponse
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.remote.onError
import com.gurumlab.vocaroutine.data.source.remote.onException
import com.gurumlab.vocaroutine.data.source.remote.onSuccess
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val userDataSource: UserDataSource
) {

    suspend fun getMyLists(uid: String): ApiResponse<Map<String, ListInfo>> {
        return apiClient.getLists(uid)
    }

    suspend fun getSharedListByCreator(uid: String): ApiResponse<Map<String, SharedListInfo>> {
        return apiClient.getSharedListByCreator("\"creator\"", "\"${uid}\"")
    }

    suspend fun deleteMyList(uid: String) {
        apiClient.deleteAllMyLists(uid)
    }

    suspend fun deleteSharedList(uid: String) {
        val result = apiClient.getSharedListByCreator("\"creator\"", "\"${uid}\"")
        result.onSuccess {
            it.keys.forEach { key ->
                apiClient.deleteSharedList(key)
            }
        }.onError { code, message ->
            Log.d("SettingRepository", "Error code: $code message: $message")
        }.onException { throwable ->
            Log.d("SettingRepository", "Exception: $throwable")
        }
    }

    suspend fun getUid(): String {
        return userDataSource.getUid()
    }

    suspend fun setUid(uid: String) {
        userDataSource.setUid(uid)
    }
}