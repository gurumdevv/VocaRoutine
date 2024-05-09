package com.gurumlab.vocaroutine.data.source.repository

import com.gurumlab.vocaroutine.data.source.remote.ApiResponse
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.Review
import com.gurumlab.vocaroutine.data.source.local.AlarmDao
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val userDataSource: UserDataSource,
    private val dao: AlarmDao
) {

    suspend fun getUid(): String {
        return userDataSource.getUid()
    }

    suspend fun getReviewListIds(currentDate: String): List<String> {
        return dao.getListIdsByDate(currentDate)
    }

    suspend fun getListsById(
        uid: String,
        reviewListId: String
    ): ApiResponse<Map<String, ListInfo>> {
        return apiClient.getListsById(uid, "\"id\"", "\"${reviewListId}\"")
    }

    suspend fun getAlarmCode(id: String, currentDate: String): Int{
        return dao.getAlarmCode(id, currentDate)
    }

    suspend fun deleteAlarm(listId: String, currentDate: String) {
        dao.deleteAlarmFromIdAndDate(listId, currentDate)
    }

    suspend fun deleteOutOfDateReviews(currentDate: String) {
        dao.deleteOutOfDateAlarms(currentDate)
    }

    suspend fun updateFirstReviewCount(uid: String, listKey: String, review: Review) {
        apiClient.updateFirstReviewCount(uid, listKey, review)
    }

    suspend fun updateSecondReviewCount(uid: String, listKey: String, review: Review) {
        apiClient.updateSecondReviewCount(uid, listKey, review)
    }

    suspend fun updateThirdReviewCount(uid: String, listKey: String, review: Review) {
        apiClient.updateThirdReviewCount(uid, listKey, review)
    }
}