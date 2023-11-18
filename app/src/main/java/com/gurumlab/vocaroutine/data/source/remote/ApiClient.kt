package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiClient {

    @GET("privateList/{uid}.json")
    suspend fun getLists(@Path("uid") uid: String): Response<Map<String, ListInfo>?>

    @GET("sharedList.json")
    suspend fun getSharedList(): Response<Map<String, SharedListInfo>?>

    @GET("sharedList.json")
    suspend fun getSharedListByCreator(
        @Query("orderBy") orderBy: String,
        @Query("equalTo") equalTo: String
    ): Map<String, SharedListInfo>

    @POST("privateList/{uid}.json")
    suspend fun uploadList(@Path("uid") uid: String, @Body newList: ListInfo)

    @POST("sharedList.json")
    suspend fun shareList(@Body newList: SharedListInfo)
}