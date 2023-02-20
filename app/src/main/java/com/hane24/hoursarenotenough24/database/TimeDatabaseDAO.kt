package com.hane24.hoursarenotenough24.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface TimeDatabaseDAO {
    @Query("SELECT * FROM tagging_log")
    fun getAll(): List<TimeDatabaseDto>

//    @Query("")
//    fun getMonth(target: String)

    @Insert(onConflict = REPLACE)
    fun insertAll(vararg times: TimeDatabaseDto)

    @Delete
    fun delete(time: TimeDatabaseDto)
}