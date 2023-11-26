package com.gurumlab.vocaroutine.data.source.repository

import com.gurumlab.vocaroutine.data.model.Alarm
import com.gurumlab.vocaroutine.data.source.remote.ApiResponse
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.local.AlarmDao
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import javax.inject.Inject

class DetailListRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val dao: AlarmDao,
    private val userDataSource: UserDataSource
) {

    suspend fun addAlarm(alarm: Alarm) {
        dao.addAlarm(alarm)
    }

    suspend fun deleteAlarm(alarmCode: Int) {
        dao.deleteAlarm(alarmCode)
    }

    suspend fun searchActiveAlarms(alarms: IntArray): List<Int> {
        return dao.searchActiveAlarms(alarms)
    }

    suspend fun getUid(): String {
        return userDataSource.getUid()
    }

    suspend fun shareList(
        sharedListInfo: SharedListInfo) {
        apiClient.shareList(sharedListInfo)
    }

    suspend fun getSharedListById(postId: String): ApiResponse<Map<String, SharedListInfo>> {
        return apiClient.getSharedListById("\"identifier\"", "\"${postId}\"")
    }
}