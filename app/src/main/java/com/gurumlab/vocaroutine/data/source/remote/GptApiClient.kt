package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.ChatRequest
import com.gurumlab.vocaroutine.data.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GptApiClient {

    @POST("v1/chat/completions")
    suspend fun getResponse(@Body requestBody: ChatRequest): ChatResponse
}