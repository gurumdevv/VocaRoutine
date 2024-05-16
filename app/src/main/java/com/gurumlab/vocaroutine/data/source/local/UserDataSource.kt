package com.gurumlab.vocaroutine.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserDataSource @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private val stringKey = stringPreferencesKey("uid")

    suspend fun getUid(): String {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[stringKey] ?: ""
            }.first()
    }

    suspend fun setUid(uid: String) {
        dataStore.edit { preferences ->
            preferences[stringKey] = uid
        }
    }
}