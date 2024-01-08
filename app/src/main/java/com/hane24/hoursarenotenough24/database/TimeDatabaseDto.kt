package com.hane24.hoursarenotenough24.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.hane24.hoursarenotenough24.data.TimeLogItem
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "tagging_log", primaryKeys = ["date", "inTimeStamp", "outTimeStamp"])
data class TagLogDto(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long,
    @ColumnInfo
    val date: String,
    @ColumnInfo
    val inTimeStamp: Long,
    @ColumnInfo
    val outTimeStamp: Long,
    @ColumnInfo
    val duration: Long?,
    @ColumnInfo
    val updateTime: Long
)

@Entity(tableName = "accumulation_time", primaryKeys = ["date"])
data class AccumulationTimeDto(
    @ColumnInfo
    val date: String,
    @ColumnInfo
    val totalAccumulationTime: Long?,
    @ColumnInfo
    val acceptedAccumulationTime: Long?,
)
