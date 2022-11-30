package com.hane24.hoursarenotenough24.data

import com.google.gson.annotations.SerializedName

data class MainInfo(
    val login: String,
    val profileImage: String,
    val inoutState: String,
)

data class InOutTimeInfo(
    val inOutLogs: List<InOutTimeItem>
)

data class InOutTimeItem(
    val inTimeStamp: Long,
    val outTimeStamp: Long,
    val durationSecond: Long
)

data class AccumulationTimeInfo(
    @SerializedName("todayAccumationTime")
    val todayAccumulationTime: Long,
    @SerializedName("monthAccumationTime")
    val monthAccumulationTime: Long
)