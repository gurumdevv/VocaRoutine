package com.gurumlab.vocaroutine.data.source.repository

import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.Review
import com.gurumlab.vocaroutine.data.source.local.AlarmDao
import com.gurumlab.vocaroutine.data.source.local.UserDataSource
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import com.gurumlab.vocaroutine.data.source.remote.onError
import com.gurumlab.vocaroutine.data.source.remote.onException
import com.gurumlab.vocaroutine.data.source.remote.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val userDataSource: UserDataSource,
    private val dao: AlarmDao
) {

    suspend fun getReviewListIds(currentDate: String): List<String> {
        return dao.getListIdsByDate(currentDate)
    }

    fun getListsById(
        uid: String,
        userToken: String,
        reviewListId: String,
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, ListInfo>> = flow {
        val response = apiClient.getListsById(uid, userToken, "\"id\"", "\"${reviewListId}\"")
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onException(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    suspend fun getAlarmCode(id: String, currentDate: String): Int {
        return dao.getAlarmCode(id, currentDate)
    }

    suspend fun deleteAlarm(listId: String, currentDate: String) {
        dao.deleteAlarmFromIdAndDate(listId, currentDate)
    }

    suspend fun deleteOutOfDateReviews(currentDate: String) {
        dao.deleteOutOfDateAlarms(currentDate)
    }

    suspend fun updateFirstReviewCount(
        uid: String,
        userToken: String,
        listKey: String,
        review: Review
    ) {
        apiClient.updateFirstReviewCount(uid, listKey, userToken, review)
    }

    suspend fun updateSecondReviewCount(
        uid: String,
        userToken: String,
        listKey: String,
        review: Review
    ) {
        apiClient.updateSecondReviewCount(uid, listKey, userToken, review)
    }

    suspend fun updateThirdReviewCount(
        uid: String,
        userToken: String,
        listKey: String,
        review: Review
    ) {
        apiClient.updateThirdReviewCount(uid, listKey, userToken, review)
    }

    suspend fun getUid(): String {
        return userDataSource.getUid()
    }

    suspend fun getUserToken(): String {
        return userDataSource.getUserToken()
    }
}