package com.gurumlab.vocaroutine.data.source.repository

import com.gurumlab.vocaroutine.data.source.local.UserDataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserDataSource @Inject constructor(private val dataStore: UserDataStore) {
    suspend fun getUid(): String {
        return dataStore.getUid.first()
    }

    suspend fun setUid(uid: String) {
        dataStore.setUid(uid)
    }
}