package com.hane24.hoursarenotenough24.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hane24.hoursarenotenough24.network.InOutTimeItem

@Entity(tableName = "tagging_log")
data class TimeDatabaseDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo
    val date: String,
    @ColumnInfo
    val inTimeStamp: Long,
    @ColumnInfo
    val outTimeStamp: Long,
    @ColumnInfo
    val duration: Long,
    @ColumnInfo
    val updateTime: Long
)
