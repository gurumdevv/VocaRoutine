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
                        SYSTEM,
                        REQUEST_COMMAND_START + "\'${word}\'" + REQUEST_COMMAND_END
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

    companion object {
        const val SYSTEM = "system"
        const val REQUEST_COMMAND_START = "너는 어원 사전이야. response는 개행문자가 포함되서는 안되고 총 글자 수가 100자가 넘겨서는 안돼."
        const val REQUEST_COMMAND_END =
            "에 대해서 단어가 분해가 가능하면 먼저 분해를 하고, " +
                    "각 분해된 요소에 대해서 뜻만 설명해주고 \"의미합니다\"로 문장을 끝내줘." +
                    "각 분해된 요소에 대해서 뜻을 설명했으면 더 이상 설명을 작성할 필요 없어." +
                    "모든 문장의 끝은 항상 \"니다\"로 끝내줘."
    }
}