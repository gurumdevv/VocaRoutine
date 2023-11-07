package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.ListInfo

class MyListRepository(private val apiClient: ApiClient) {

    suspend fun getLists(): List<ListInfo> {
        return apiClient.getLists()
    }
}