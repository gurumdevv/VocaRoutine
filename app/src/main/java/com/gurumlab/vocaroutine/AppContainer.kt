package com.gurumlab.vocaroutine

class AppContainer {

    private var apiClient: ApiClient? = null
    private var gptApiClient: GptApiClient? = null

    fun provideApiClient(): ApiClient {
        return apiClient ?: ApiClient.create().apply {
            apiClient = this
        }
    }

    fun provideGptApiClient(apiKey: String): GptApiClient {
        return gptApiClient ?: GptApiClient.create(apiKey).apply {
            gptApiClient = this
        }
    }
}