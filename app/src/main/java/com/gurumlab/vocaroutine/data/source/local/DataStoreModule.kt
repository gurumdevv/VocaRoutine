package com.gurumlab.vocaroutine.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreModule (private val context: Context){

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dataStore")

    private val userToken = stringPreferencesKey("USER_TOKEN")

    val savedUserToken: Flow<String> = context.dataStore.data
        .catch { exception ->
            if(exception is IOException){
                emit(emptyPreferences())
            } else{
                throw exception
            }
        }.map {
            it[userToken] ?: ""
        }

    suspend fun setUserToken(userTokenString: String){
        context.dataStore.edit {
            it[userToken] = userTokenString
        }
    }
}