package com.hane24.hoursarenotenough24.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hane24.hoursarenotenough24.data.TimeLogItem
import com.hane24.hoursarenotenough24.network.InOutTimeContainer
import com.hane24.hoursarenotenough24.network.InOutTimeItem
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "tagging_log", primaryKeys = ["date", "inTimeStamp"])
data class TimeDatabaseDto(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long,
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

fun List<TimeDatabaseDto>.asDomainModel(): List<TimeLogItem> {
    val format = SimpleDateFormat("dd HH mm ss", Locale("ko", "KR"))
    return map { log ->
        var day: Int
        val inString =
            format.format(log.inTimeStamp * 1000).split(' ').let {
                day = it[0].toInt()
                "${it[1]}:${it[2]}:${it[3]}"
            }
        val outString =
            format.format(log.outTimeStamp * 1000).split(' ').let { "${it[1]}:${it[2]}:${it[3]}" }
        TimeLogItem(day, inString, outString, log.duration)
    }
}
