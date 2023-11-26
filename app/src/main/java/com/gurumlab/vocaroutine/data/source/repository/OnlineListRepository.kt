package com.gurumlab.vocaroutine.data.source.repository

import com.gurumlab.vocaroutine.data.source.remote.ApiResponse
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import javax.inject.Inject

class OnlineListRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val userDataSource: UserDataSource
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
        return userDataSource.getUid()
    }
}