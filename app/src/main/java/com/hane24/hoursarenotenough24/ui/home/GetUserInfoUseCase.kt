package com.hane24.hoursarenotenough24.ui.home

import com.hane24.hoursarenotenough24.network.AccumulationTimeInfo
import com.hane24.hoursarenotenough24.network.MainInfo
import com.hane24.hoursarenotenough24.repository.UserRepository

class GetUserInfoUseCase(
    private val userRepository: UserRepository
) {
    suspend fun getInfo(): MainInfo {
        return userRepository.getInfo()
    }

    suspend fun getAccumulationTime(): AccumulationTimeInfo {
        return userRepository.getAccumulationTime()
    }
}