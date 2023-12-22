package com.hane24.hoursarenotenough24.widget

import com.hane24.hoursarenotenough24.network.AccumulationTimeInfo
import com.hane24.hoursarenotenough24.network.Hane24Api
import com.hane24.hoursarenotenough24.overview.ParseTimeUseCase
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtilss
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import retrofit2.HttpException

enum class WidgetState {
    INIT,
    LOADING,
    LOGIN_ERROR,
    UNKNOWN_ERROR,
    COMPLETE
}

class WidgetViewModel(
    private val hane24Api: Hane24Api,
    private val sharedPreferenceUtilss: SharedPreferenceUtilss
) {
    private val _monthAccumulationTime = MutableStateFlow(0L)
    val monthAccumulationTimeInfo: StateFlow<Pair<String, String>> =
        _monthAccumulationTime.map { ParseTimeUseCase().parseAccumulationTime(it) }
            .stateIn(CoroutineScope(Dispatchers.Default), SharingStarted.Lazily, "0" to "0")

    private val _acceptedAccumulationTime = MutableStateFlow(0L)
    val acceptedAccumulationTime: StateFlow<Pair<String, String>> =
        _acceptedAccumulationTime.map { ParseTimeUseCase().parseAccumulationTime(it) }
            .stateIn(CoroutineScope(Dispatchers.Default), SharingStarted.Lazily, "0" to "0")

    private val _state = MutableStateFlow<WidgetState>(WidgetState.INIT)
    val state = _state.asStateFlow()

    fun updateLoading() {
        _state.value = WidgetState.LOADING
    }

    suspend fun refresh() {
        val accTimeInfo: AccumulationTimeInfo =
            hane24Api.getAccumulationTime(sharedPreferenceUtilss.getAccessToken())

        _monthAccumulationTime.value = accTimeInfo.monthAccumulationTime
        _acceptedAccumulationTime.value = accTimeInfo.monthlyAcceptedAccumulationTime
    }

    fun updateError(err: Exception) {
        if (err is HttpException && err.code() == 401) {
            _state.value = WidgetState.LOGIN_ERROR
        } else {
            _state.value = WidgetState.UNKNOWN_ERROR
        }
    }

    suspend fun refreshComplete() {
        if (state.value == WidgetState.LOGIN_ERROR || state.value == WidgetState.UNKNOWN_ERROR) {
            delay(2000)
        }
        _state.value = WidgetState.COMPLETE
    }
}