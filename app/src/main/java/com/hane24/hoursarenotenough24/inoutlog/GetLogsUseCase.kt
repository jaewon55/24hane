package com.hane24.hoursarenotenough24.inoutlog

import com.hane24.hoursarenotenough24.data.AccumulationTimeWithTagLog
import com.hane24.hoursarenotenough24.repository.TimeDBRepository
import com.hane24.hoursarenotenough24.repository.TimeServerRepository

class GetLogsUseCase(
    private val timeServerRepository: TimeServerRepository,
    private val timeDBRepository: TimeDBRepository
) {
    suspend operator fun invoke(year: Int, month: Int): AccumulationTimeWithTagLog {
        var tagLog = timeDBRepository.getAccumulationTimeWithLogs(year, month)
        if (tagLog.tagLogs.isEmpty() || tagLog.totalAccumulationTime < 0 || tagLog.acceptedAccumulationTime < 0) {
            tagLog = timeServerRepository.getTagLogPerMonth(year, month)
            timeDBRepository.insert(
                tagLog.asDatabaseTagLogDto(year, month),
                tagLog.asDatabaseAccumulationTimeDto(year, month)
            )
        }
        return tagLog
    }
}