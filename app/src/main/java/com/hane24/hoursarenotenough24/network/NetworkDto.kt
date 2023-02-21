package com.hane24.hoursarenotenough24.network

import com.google.gson.annotations.SerializedName
import com.hane24.hoursarenotenough24.data.TimeLogItem
import com.hane24.hoursarenotenough24.database.TimeDatabaseDto
import java.text.SimpleDateFormat
import java.util.*

data class MainInfo(
    val login: String,
    val profileImage: String,
    val inoutState: String,
    val tagAt: String
)

data class AccumulationTimeInfo(
    @SerializedName("todayAccumationTime")
    val todayAccumulationTime: Long,
    @SerializedName("monthAccumationTime")
    val monthAccumulationTime: Long
)

data class InOutTimeContainer(
    val inOutLogs: List<InOutTimeItem>
)

data class InOutTimeItem(
    val inTimeStamp: Long,
    val outTimeStamp: Long,
    val durationSecond: Long
)

fun InOutTimeContainer.asDatabaseDto(date: String): List<TimeDatabaseDto> {
    val format = SimpleDateFormat("yyyyMMdd", Locale("ko", "KR"))
    return if (inOutLogs.isEmpty()) {
        listOf(
            TimeDatabaseDto(
                0,
                date,
                0,
                0,
                0,
                System.currentTimeMillis()
            )
        )
    } else {
        inOutLogs.map { log ->
            val date = format.format(log.inTimeStamp * 1000)
            TimeDatabaseDto(
                0,
                date,
                log.inTimeStamp,
                log.outTimeStamp,
                log.durationSecond,
                System.currentTimeMillis()
            )
        }
    }
}

fun InOutTimeContainer.asDomainModel(): List<TimeLogItem> {
    val format = SimpleDateFormat("dd HH mm ss", Locale("ko", "KR"))
    return inOutLogs.map { log ->
        var day: Int
        val inString =
            format.format(log.inTimeStamp * 1000).split(' ').let {
                day = it[0].toInt()
                "${it[1]}:${it[2]}:${it[3]}"
            }
        val outString =
            format.format(log.outTimeStamp * 1000).split(' ').let { "${it[1]}:${it[2]}:${it[3]}" }
        TimeLogItem(day, inString, outString, log.durationSecond)
    }
}

