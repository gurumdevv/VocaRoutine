package com.gurumlab.vocaroutine.data.source.repository

import com.gurumlab.vocaroutine.data.model.Alarm
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.source.remote.ApiResponse
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.local.AlarmDao
import com.gurumlab.vocaroutine.data.source.local.OfflineModeDao
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import javax.inject.Inject

class DetailListRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val alarmDao: AlarmDao,
    private val offlineModeDao: OfflineModeDao,
    private val userDataSource: UserDataSource
) {

    suspend fun addAlarm(alarm: Alarm) {
        alarmDao.addAlarm(alarm)
    }

    suspend fun deleteAlarm(alarmCode: Int) {
        alarmDao.deleteAlarm(alarmCode)
    }

    suspend fun searchActiveAlarms(alarms: IntArray): List<Int> {
        return alarmDao.searchActiveAlarms(alarms)
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

    suspend fun downloadListOnDevice(listInfo: ListInfo){
        offlineModeDao.insertListInfo(listInfo)
    }

    suspend fun deleteListOnDevice(listInfo: ListInfo){
        offlineModeDao.deleteListInfo(listInfo)
    }

    suspend fun getListById(id: String): String{
        return offlineModeDao.getListById(id)
    }
}