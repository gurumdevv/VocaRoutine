package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.SharedListInfo
import javax.inject.Inject

class OnlineListRepository @Inject constructor(
    private val apiClient: ApiClient
) {

    suspend fun getLists(): List<SharedListInfo> {
        return apiClient.getSharedList().values.toList()
    }
}