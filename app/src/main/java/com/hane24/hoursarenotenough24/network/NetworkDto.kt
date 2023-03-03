package com.hane24.hoursarenotenough24.network

import com.google.gson.annotations.SerializedName
import com.hane24.hoursarenotenough24.data.TimeLogItem
import com.hane24.hoursarenotenough24.database.TimeDatabaseDto
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import java.text.SimpleDateFormat
import java.util.*

data class MainInfo(
    val login: String,
    val profileImage: String,
    val inoutState: String,
    val tagAt: String,
    val gaepo: Int,
    val seocho: Int
)

data class AccumulationTimeInfo(
    val todayAccumulationTime: Long,
    val monthAccumulationTime: Long,
    val sixWeekAccumulationTime: List<Long>,
    val sixMonthAccumulationTime: List<Long>
)

data class InOutTimeContainer(
    val inOutLogs: List<InOutTimeItem>
)

data class InOutTimeItem(
    val inTimeStamp: Long?,
    val outTimeStamp: Long?,
    val durationSecond: Long?
)
data class ClusterPopulationInfo(
    val cluster: String,
    @SerializedName("cadet")
    val population: Int
)

data class ReissueState(
    val state: String
)
data class ReissueRequestResult(
    val login: String,
    val request_at: String
)

fun InOutTimeContainer.asDatabaseDto(date: String): List<TimeDatabaseDto> {
    val format = SimpleDateFormat("yyyyMMdd", Locale("ko", "KR"))
    return if (inOutLogs.isEmpty()) {
        listOf(
            TimeDatabaseDto(
//                0,
                date + "00",
                0,
                0,
                0,
                System.currentTimeMillis()
            )
        )
    } else {
        inOutLogs.map { log ->
            val dateOfLog = when {
                log.inTimeStamp != null -> format.format(log.inTimeStamp * 1000)
                log.outTimeStamp != null -> format.format(log.outTimeStamp * 1000)
                else -> date + "00"
            }
            TimeDatabaseDto(
//                0,
                dateOfLog,
                log.inTimeStamp ?: 0,
                log.outTimeStamp ?: 0,
                log.durationSecond,
                System.currentTimeMillis()
            )
        }
    }
}

//fun InOutTimeContainer.asDomainModel(): List<TimeLogItem> {
//    val format = SimpleDateFormat("dd HH mm ss", Locale("ko", "KR"))
//    return inOutLogs.map { log ->
//        var day: Int
//        val inString =
//            format.format(log.inTimeStamp * 1000).split(' ').let {
//                day = it[0].toInt()
//                "${it[1]}:${it[2]}:${it[3]}"
//            }
//        val outString =
//            format.format(log.outTimeStamp * 1000).split(' ').let { "${it[1]}:${it[2]}:${it[3]}" }
//        TimeLogItem(day, inString, outString, log.durationSecond)
//    }
//}

