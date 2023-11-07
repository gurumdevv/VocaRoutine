package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.ListInfo
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiClient {

    @GET("myList.json")
    suspend fun getLists(): List<ListInfo>

    @POST("privateList/{uid}.json")
    suspend fun uploadList(@Path("uid") uid: String, @Body newList: ListInfo)

    companion object {
        private val moshi = Moshi.Builder()
            .build()

        private const val BASE_URL =
            "https://vocaroutine-default-rtdb.asia-southeast1.firebasedatabase.app"

        fun create(): ApiClient {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(ApiClient::class.java)
        }
    }
}