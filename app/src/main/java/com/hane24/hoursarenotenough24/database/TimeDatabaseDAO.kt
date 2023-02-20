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
    fun getMonth(target: String)

    @Query("SELECT * FROM tagging_log WHERE date LIKE :target")
    fun getDay(target: String)

    @Insert(onConflict = REPLACE)
    fun insertAll(vararg times: TimeDatabaseDto)

    @Query("DELETE FROM tagging_log")
    fun deleteAll()
}