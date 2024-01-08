package com.hane24.hoursarenotenough24.data

import com.hane24.hoursarenotenough24.database.AccumulationTimeDto
import com.hane24.hoursarenotenough24.database.TagLogDto
import java.text.SimpleDateFormat
import java.util.*

data class TimeLogItem(
    val day: Int,
    val inTime: String,
    val outTime: String,
    val durationTime: Long?
)

data class TagLog(
    val inTimeStamp: Long?,
    val outTimeStamp: Long?,
    val durationSecond: Long?
)

data class AccumulationTimeWithTagLog(
    val totalAccumulationTime: Long,
    val acceptedAccumulationTime: Long,
    val tagLogs: List<TagLog>,
) {
    fun asDatabaseAccumulationTimeDto(year: Int, month: Int): AccumulationTimeDto {
        val date = String.format("%4d%02d", year, month)
        return AccumulationTimeDto(date, totalAccumulationTime, acceptedAccumulationTime)
    }

    fun asDatabaseTagLogDto(year: Int, month: Int): List<TagLogDto> {
        val date = String.format("%4d%02d", year, month)
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale("ko", "KR"))
        return if (tagLogs.isEmpty()) {
            listOf(
                TagLogDto(
                    date + "00", 0, 0, 0, System.currentTimeMillis()
                )
            )
        } else {
            tagLogs.map { log ->
                val dateOfLog = when {
                    log.inTimeStamp != null -> simpleDateFormat.format(log.inTimeStamp * 1000)
                    log.outTimeStamp != null -> simpleDateFormat.format(log.outTimeStamp * 1000)
                    else -> date + "00"
                }
                TagLogDto(
                    dateOfLog,
                    log.inTimeStamp ?: 0,
                    log.outTimeStamp ?: 0,
                    log.durationSecond,
                    System.currentTimeMillis()
                )
            }
        }
    }
}
