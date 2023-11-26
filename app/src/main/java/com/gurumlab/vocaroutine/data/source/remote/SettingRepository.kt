package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.local.DataStoreModule
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val dataStore: DataStoreModule
) {

    suspend fun getMyLists(uid: String): List<ListInfo>? {
        val response = apiClient.getLists(uid).body()

        return if (response.isNullOrEmpty()) {
            null
        } else {
            response.values.toList()
        }
    }

    suspend fun getSharedListByCreator(uid: String): List<SharedListInfo> {
        return apiClient.getSharedListByCreator("\"creator\"", "\"${uid}\"").values.toList()
    }

    suspend fun deleteMyList(uid: String) {
        apiClient.deleteMyList(uid)
    }

    suspend fun deleteSharedList(uid: String) {
        val listKey = apiClient.getSharedListByCreator("\"creator\"", "\"${uid}\"").keys
        listKey.forEach { key ->
            apiClient.deleteSharedList(key)
        }
    }

    suspend fun getUid(): String {
        return dataStore.getUid.first()
    }

    suspend fun setUid(uid: String) {
        dataStore.setUid(uid)
    }
}