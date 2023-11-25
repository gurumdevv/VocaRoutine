package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.ApiResponse
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.source.local.AlarmDao
import com.gurumlab.vocaroutine.data.source.repository.UserDataSource
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
        return dao.searchListByDate(currentDate)
    }

    suspend fun getListsById(
        uid: String,
        reviewListId: String
    ): ApiResponse<Map<String, ListInfo>> {
        return apiClient.getListsById(uid, "\"id\"", "\"${reviewListId}\"")
    }

    suspend fun deleteAlarm(listId: String, currentDate: String) {
        dao.deleteAlarmFromIdAndDate(listId, currentDate)
    }

    suspend fun deleteOutOfDateReviews(currentDate: String) {
        dao.deleteOutOfDateAlarms(currentDate)
    }
}