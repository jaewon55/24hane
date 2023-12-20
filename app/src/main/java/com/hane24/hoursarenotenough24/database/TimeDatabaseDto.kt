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

fun List<TagLogDto>.asDomainModel(): List<TimeLogItem> {
    val format = SimpleDateFormat("dd HH mm ss", Locale("ko", "KR"))
    return map { log ->
        val day = log.date.substring(6..7).toInt()
        val inString = log.inTimeStamp.let { timeNumber ->
            if (timeNumber == 0L) {
                "-"
            } else {
                format.format(timeNumber * 1000).split(' ').let { list ->
                    "${list[1]}:${list[2]}:${list[3]}"
                }
            }
        }
        val outString = log.outTimeStamp.let { timeNumber ->
            if (timeNumber == 0L) {
                "-"
            } else {
                format.format(timeNumber * 1000).split(' ').let { list ->
                    "${list[1]}:${list[2]}:${list[3]}"
                }
            }
        }
        TimeLogItem(day, inString, outString, log.duration)
    }
}
