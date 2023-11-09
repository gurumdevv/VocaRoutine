package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.source.local.DataStoreModule
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MyListRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val dataStore: DataStoreModule
) {

    suspend fun getLists(uid: String): List<ListInfo> {
        return apiClient.getLists(uid).values.toList()
    }

    suspend fun getUid(): String {
        return dataStore.getUid.first()
    }
}