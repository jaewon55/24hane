package com.hane24.hoursarenotenough24.overview

class ParseTimeUseCase {
    fun parseTargetTime(time: Int): Int {
        return time / 3600
    }

    fun parseAccumulationTime(accumulationTime: Long?): Pair<String, String> {
        val time = accumulationTime ?: 0
        var second = time
        val hour = second / 3600

        second -= hour * 3600
        val min = second / 60

        return String.format("%d", hour) to String.format("%d", min)
    }
}