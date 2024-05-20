package com.gurumlab.vocaroutine.data.source.repository

import com.gurumlab.vocaroutine.data.model.ChatMessage
import com.gurumlab.vocaroutine.data.model.ChatRequest
import com.gurumlab.vocaroutine.data.model.ChatResponse
import com.gurumlab.vocaroutine.data.source.local.UserDataSource
import com.gurumlab.vocaroutine.data.source.remote.GptApiClient
import com.gurumlab.vocaroutine.data.source.remote.onError
import com.gurumlab.vocaroutine.data.source.remote.onException
import com.gurumlab.vocaroutine.data.source.remote.onSuccess
import com.gurumlab.vocaroutine.di.GptVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MakingListRepository @Inject constructor(
    private val gptApiClient: GptApiClient,
    private val userDataSource: UserDataSource
) {

    suspend fun getEtymology(
        word: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<ChatResponse> = flow {
        val response = gptApiClient.getResponse(
            ChatRequest(
                GptVersion.CURRENT_VERSION, listOf(
                    ChatMessage(
                        "system",
                        "\'${word}\'를 분해해서 각 의미에 대해서 100자 이내로 어원만 알려줘. 문장의 끝은 \"니다\"로 끝내줘"
                    )
                )
            )
        )
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onException(it.message)
        }
    }.flowOn(Dispatchers.Default)

    suspend fun getUid(): String {
        return userDataSource.getUid()
    }
}