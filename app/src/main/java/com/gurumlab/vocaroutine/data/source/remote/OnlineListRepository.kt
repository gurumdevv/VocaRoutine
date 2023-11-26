package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.local.DataStoreModule
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class OnlineListRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val dataStore: DataStoreModule
) {

    suspend fun getSharedLists(): List<SharedListInfo>? {
        val response = apiClient.getSharedList().body()

        return if (response.isNullOrEmpty()) {
            null
        } else {
            response.values.toList()
        }
    }

    suspend fun uploadList(uid: String, listInfo: ListInfo) {
        apiClient.uploadList(uid, listInfo)
    }

    suspend fun getMyLists(uid: String): List<ListInfo>? {
        val response = apiClient.getLists(uid).body()

        return if (response.isNullOrEmpty()) {
            null
        } else {
            response.values.toList()
        }
    }

    suspend fun getUid(): String {
        return dataStore.getUid.first()
    }
}