package com.gurumlab.vocaroutine.data.source.repository

import com.gurumlab.vocaroutine.data.model.Alarm
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.SharedListInfo
import com.gurumlab.vocaroutine.data.source.local.AlarmDao
import com.gurumlab.vocaroutine.data.source.local.OfflineModeDao
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import com.gurumlab.vocaroutine.data.source.remote.onError
import com.gurumlab.vocaroutine.data.source.remote.onException
import com.gurumlab.vocaroutine.data.source.remote.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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

    suspend fun shareList(sharedListInfo: SharedListInfo) {
        apiClient.shareList(sharedListInfo)
    }

    suspend fun getSharedListById(
        postId: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, SharedListInfo>> = flow {
        val response = apiClient.getSharedListById("\"identifier\"", "\"${postId}\"")
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onException(it.message)
        }
    }.flowOn(Dispatchers.Default)

    suspend fun downloadListOnDevice(listInfo: ListInfo) {
        offlineModeDao.insertListInfo(listInfo)
    }

    suspend fun deleteListOnDevice(listInfo: ListInfo) {
        offlineModeDao.deleteListInfo(listInfo)
    }

    suspend fun getListById(id: String): String? {
        return offlineModeDao.getListById(id)
    }
}