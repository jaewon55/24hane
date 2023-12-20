package com.hane24.hoursarenotenough24.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface TimeDatabaseDAO {
    /* TagLog Query */
    @Query("SELECT * FROM tagging_log WHERE date LIKE :date || '%'")
    suspend fun getTagLogMonth(date: String): List<TagLogDto>

    @Insert(onConflict = REPLACE)
    suspend fun insertTagLogAll(vararg times: TagLogDto)

    @Query("DELETE FROM tagging_log")
    suspend fun deleteTagLogAll()


    /* AccumulationTime Query */
    @Query("SELECT * FROM accumulation_time WHERE date LIKE :date")
    suspend fun getAccumulationTime(date: String): AccumulationTimeDto?

    @Insert(onConflict = REPLACE)
    suspend fun insertAccumulationTime(accumulationTime: AccumulationTimeDto)

    @Query("DELETE FROM accumulation_time")
    suspend fun deleteAccumulationTimeAll()

}