package com.gurumlab.vocaroutine.data.source.remote

import com.gurumlab.vocaroutine.data.model.Alarm
import com.gurumlab.vocaroutine.data.model.ApiResponse
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.local.AppDatabase
import com.gurumlab.vocaroutine.data.source.local.DataStoreModule
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DetailListRepository @Inject constructor(
    database: AppDatabase,
    private val apiClient: ApiClient,
    private val dataStore: DataStoreModule
) {

    private val dao = database.alarmDao()

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
        return dataStore.getUid.first()
    }

    suspend fun shareList(sharedListInfo: SharedListInfo) {
        apiClient.shareList(sharedListInfo)
    }

    suspend fun getSharedListById(postId: String): ApiResponse<Map<String, SharedListInfo>> {
        return apiClient.getSharedListById("\"identifier\"", "\"${postId}\"")
    }
}