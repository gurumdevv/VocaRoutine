package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.ApiResponse
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.local.DataStoreModule
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class OnlineListRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val dataStore: DataStoreModule
) {

    suspend fun getSharedLists(): ApiResponse<Map<String, SharedListInfo>> {
        return apiClient.getSharedList()
    }

    suspend fun uploadList(uid: String, listInfo: ListInfo) {
        apiClient.uploadList(uid, listInfo)
    }

    suspend fun getMyLists(uid: String): ApiResponse<Map<String, ListInfo>> {
        return apiClient.getLists(uid)
    }

    suspend fun getUid(): String {
        return dataStore.getUid.first()
    }
}