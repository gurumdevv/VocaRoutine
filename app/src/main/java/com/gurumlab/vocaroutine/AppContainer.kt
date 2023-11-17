package com.gurumlab.vocaroutine

class AppContainer {

    private var apiClient: ApiClient? = null

    fun provideApiClient(): ApiClient {
        return apiClient ?: ApiClient.create().apply {
            apiClient = this
        }
    }
}