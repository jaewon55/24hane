package com.hane24.hoursarenotenough24.overview

class CalculateProgressUseCase {
    operator fun invoke(time: Long?, targetTime: Int?): Int {
        val totalTime = time ?: 0
        val targetDouble = targetTime?.toDouble() ?: 1.0

        return (totalTime / targetDouble * 100).toInt()
    }
}