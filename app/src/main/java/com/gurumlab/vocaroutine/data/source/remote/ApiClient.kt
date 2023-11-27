package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.Review
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiClient {

    @GET("privateList/{uid}.json")
    suspend fun getLists(@Path("uid") uid: String): ApiResponse<Map<String, ListInfo>>

    @GET("privateList/{uid}.json")
    suspend fun getListsById(
        @Path("uid") uid: String,
        @Query("orderBy") orderBy: String,
        @Query("equalTo") equalTo: String
    ): ApiResponse<Map<String, ListInfo>>

    @GET("sharedList.json")
    suspend fun getSharedList(): ApiResponse<Map<String, SharedListInfo>>

    @GET("sharedList.json")
    suspend fun getSharedListByCreator(
        @Query("orderBy") orderBy: String,
        @Query("equalTo") equalTo: String
    ): ApiResponse<Map<String, SharedListInfo>>

    @GET("sharedList.json")
    suspend fun getSharedListById(
        @Query("orderBy") orderBy: String,
        @Query("equalTo") equalTo: String
    ): ApiResponse<Map<String, SharedListInfo>>

    @POST("privateList/{uid}.json")
    suspend fun uploadList(@Path("uid") uid: String, @Body newList: ListInfo)

    @POST("sharedList.json")
    suspend fun shareList(@Body newList: SharedListInfo)

    @DELETE("privateList/{uid}/{listId}.json")
    suspend fun deleteMyList(@Path("uid") uid: String, @Path("listId") listId: String)

    @DELETE("privateList/{uid}.json")
    suspend fun deleteAllMyLists(@Path("uid") uid: String)

    @DELETE("sharedList/{uid}.json")
    suspend fun deleteSharedList(@Path("uid") uid: String)

    @PATCH("privateList/{uid}/{listKey}/review.json")
    suspend fun updateFirstReviewCount(
        @Path("uid") uid: String,
        @Path("listKey") listKey: String,
        @Body firstReview: Review
    )

    @PATCH("privateList/{uid}/{listKey}/review.json")
    suspend fun updateSecondReviewCount(
        @Path("uid") uid: String,
        @Path("listKey") listKey: String,
        @Body secondReview: Review
    )

    @PATCH("privateList/{uid}/{listKey}/review.json")
    suspend fun updateThirdReviewCount(
        @Path("uid") uid: String,
        @Path("listKey") listKey: String,
        @Body thirdReview: Review
    )
}