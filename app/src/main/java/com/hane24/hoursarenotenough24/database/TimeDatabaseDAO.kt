package com.hane24.hoursarenotenough24.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface TimeDatabaseDAO {
    @Query("SELECT * FROM tagging_log")
    fun getAll(): List<TimeDatabaseDto>

    @Query("SELECT * FROM tagging_log WHERE date LIKE :target || '%'")
    fun getMonth(target: String): List<TimeDatabaseDto>

    @Query("SELECT * FROM tagging_log WHERE date LIKE :target")
    fun getDay(target: String): List<TimeDatabaseDto>

    @Query("DELETE FROM tagging_log WHERE date = :date AND inTimeStamp = :inTime AND outTimeStamp = :outTime")
    fun deleteOne(date: String, inTime: Long, outTime: Long)

    @Insert(onConflict = REPLACE)
    fun insertAll(vararg times: TimeDatabaseDto)

    @Query("DELETE FROM tagging_log")
    fun deleteAll()
}