package com.gurumlab.vocaroutine.data.source.repository

import com.gurumlab.vocaroutine.data.model.ApiResponse
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.source.local.DataStoreModule
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MyListRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val dataStore: DataStoreModule
) {

    suspend fun getLists(uid: String): ApiResponse<Map<String, ListInfo>> {
        return apiClient.getLists(uid)
    }

    suspend fun getUid(): String {
        return dataStore.getUid.first()
    }
}