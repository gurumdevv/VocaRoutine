package com.gurumlab.vocaroutine.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserDataSource @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private val uidStringKey = stringPreferencesKey("uid")
    private val tokenStringKey = stringPreferencesKey("userToken")

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
                preferences[uidStringKey] ?: ""
            }.firstOrNull() ?: ""
    }

    suspend fun setUid(uid: String) {
        dataStore.edit { preferences ->
            preferences[uidStringKey] = uid
        }
    }

    suspend fun getUserToken(): String {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[tokenStringKey] ?: ""
            }.firstOrNull() ?: ""
    }

    suspend fun setUserToken(userToken: String) {
        dataStore.edit { preferences ->
            preferences[tokenStringKey] = userToken
        }
    }
}