package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.ChatMessage
import com.gurumlab.vocaroutine.data.model.ChatRequest
import javax.inject.Inject

class MakingListRepository @Inject constructor(private val gptApiClient: GptApiClient) {

    suspend fun getEtymology(word: String): String {
        val request = gptApiClient.getResponse(
            ChatRequest(
                "gpt-3.5-turbo-1106", listOf(
                    ChatMessage("system", "\'${word}\'에 대한 어원만 알려줘. 30자 이내로 간단하게 설명해줘.")
                )
            )
        )
        return request.choices.first().message.content
    }
}