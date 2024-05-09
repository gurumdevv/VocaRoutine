package com.gurumlab.vocaroutine.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gurumlab.vocaroutine.data.model.ListInfo

@Dao
interface OfflineModeDao {
    @Insert
    suspend fun insertListInfo(listInfo: ListInfo)

    @Update
    suspend fun updateListInfo(listInfo: ListInfo)

    @Delete
    suspend fun deleteListInfo(listInfo: ListInfo)

    @Query("SELECT * FROM ListInfo")
    suspend fun getAllListInfo(): List<ListInfo>

    @Query("SELECT id FROM ListInfo WHERE id = :id")
    suspend fun getListById(id: String): String
}