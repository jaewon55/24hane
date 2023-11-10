package com.hane24.hoursarenotenough24.inoutlog

import com.hane24.hoursarenotenough24.data.TagLog
import com.hane24.hoursarenotenough24.data.asDatabaseDto
import com.hane24.hoursarenotenough24.repository.TimeDBRepository
import com.hane24.hoursarenotenough24.repository.TimeServerRepository

class GetLogsUseCase(
    private val timeServerRepository: TimeServerRepository,
    private val timeDBRepository: TimeDBRepository
) {
    suspend operator fun invoke(year: Int, month: Int): List<TagLog> {
        var logs = timeDBRepository.getTagLogPerMonth(year, month)
        if (logs.isEmpty()) {
            logs = timeServerRepository.getTagLogPerMonth(year, month)
            timeDBRepository.insert(logs.asDatabaseDto(year, month))
        }
        return logs
    }
}