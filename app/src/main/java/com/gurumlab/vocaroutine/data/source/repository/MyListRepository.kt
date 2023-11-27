package com.gurumlab.vocaroutine.data.source.repository

import com.gurumlab.vocaroutine.data.source.remote.ApiResponse
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import javax.inject.Inject

class MyListRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val userDataSource: UserDataSource
) {

    suspend fun getLists(uid: String): ApiResponse<Map<String, ListInfo>> {
        return apiClient.getLists(uid)
    }

    suspend fun getUid(): String {
        return userDataSource.getUid()
    }

    suspend fun deleteList(uid: String, listId: String){
        apiClient.deleteMyList(uid, listId)
    }

    suspend fun getListsById(
        uid: String,
        listId: String
    ): ApiResponse<Map<String, ListInfo>> {
        return apiClient.getListsById(uid, "\"id\"", "\"${listId}\"")
    }
}