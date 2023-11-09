package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.ListInfo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiClient {

    @GET("privateList/{uid}.json")
    suspend fun getLists(@Path("uid") uid: String): Map<String, ListInfo>

    @POST("privateList/{uid}.json")
    suspend fun uploadList(@Path("uid") uid: String, @Body newList: ListInfo)
}