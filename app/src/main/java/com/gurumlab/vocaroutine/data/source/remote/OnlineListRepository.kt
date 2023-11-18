package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.SharedListInfo
import javax.inject.Inject

class OnlineListRepository @Inject constructor(
    private val apiClient: ApiClient
) {

    suspend fun getLists(): List<SharedListInfo>? {
        val response = apiClient.getSharedList().body()

        return if(response.isNullOrEmpty()){
            null
        } else{
            response.values.toList()
        }
    }
}