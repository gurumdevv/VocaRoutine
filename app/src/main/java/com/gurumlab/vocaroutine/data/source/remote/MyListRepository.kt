package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.MyList

class MyListRepository(private val apiClient: ApiClient) {

    suspend fun getLists(): List<MyList> {
        return apiClient.getLists()
    }
}