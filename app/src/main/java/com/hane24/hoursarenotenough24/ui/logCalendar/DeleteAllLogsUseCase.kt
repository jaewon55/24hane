package com.hane24.hoursarenotenough24.ui.logCalendar

import com.hane24.hoursarenotenough24.repository.TimeDBRepository

class DeleteAllLogsUseCase(
    private val timeDBRepository: TimeDBRepository
) {
    suspend operator fun invoke() {
        timeDBRepository.deleteAll()
    }
}