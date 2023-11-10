package com.hane24.hoursarenotenough24.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface TimeDatabaseDAO {
    @Query("SELECT * FROM tagging_log")
    suspend fun getAll(): List<TimeDatabaseDto>

    @Query("SELECT * FROM tagging_log WHERE date LIKE :target || '%'")
    suspend fun getMonth(target: String): List<TimeDatabaseDto>

    @Query("SELECT * FROM tagging_log WHERE date LIKE :target")
    suspend fun getDay(target: String): List<TimeDatabaseDto>

    @Query("DELETE FROM tagging_log WHERE date = :date AND inTimeStamp = :inTime AND outTimeStamp = :outTime")
    suspend fun deleteOne(date: String, inTime: Long, outTime: Long)

    @Query("DELETE FROM tagging_log WHERE date LIKE :target || '%'")
    suspend fun deleteMonth(target: String)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(vararg times: TimeDatabaseDto)

    @Query("DELETE FROM tagging_log")
    suspend fun deleteAll()
}