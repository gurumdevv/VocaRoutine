package com.gurumlab.vocaroutine.data.source.remote

import android.util.Log
import com.gurumlab.vocaroutine.data.model.ApiResponse
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.model.onError
import com.gurumlab.vocaroutine.data.model.onException
import com.gurumlab.vocaroutine.data.model.onSuccess
import com.gurumlab.vocaroutine.data.source.local.DataStoreModule
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val dataStore: DataStoreModule
) {

    suspend fun getMyLists(uid: String): ApiResponse<Map<String, ListInfo>> {
        return apiClient.getLists(uid)
    }

    suspend fun getSharedListByCreator(uid: String): ApiResponse<Map<String, SharedListInfo>> {
        return apiClient.getSharedListByCreator("\"creator\"", "\"${uid}\"")
    }

    suspend fun deleteMyList(uid: String) {
        apiClient.deleteMyList(uid)
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
        return dataStore.getUid.first()
    }

    suspend fun setUid(uid: String) {
        dataStore.setUid(uid)
    }
}