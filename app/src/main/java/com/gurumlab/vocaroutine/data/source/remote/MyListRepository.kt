package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.source.local.DataStoreModule
import kotlinx.coroutines.flow.first

class MyListRepository(private val apiClient: ApiClient, private val dataStore: DataStoreModule) {

    suspend fun getLists(uid: String): List<ListInfo> {
        return apiClient.getLists(uid).values.toList()
    }

    suspend fun getUid(): String {
        return dataStore.savedUid.first()
    }
}