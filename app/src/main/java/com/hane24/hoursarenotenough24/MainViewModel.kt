package com.hane24.hoursarenotenough24

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hane24.hoursarenotenough24.error.ExceptionHandler
import com.hane24.hoursarenotenough24.error.ExceptionHandlerFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val overViewRefresh: suspend () -> Unit,
    ): ViewModel() {

    private val _errorHandler = MutableStateFlow<ExceptionHandler?>(null)
    val errorHandler = _errorHandler.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    suspend fun refresh() {
        try {
            _loading.value = true
            overViewRefresh()
        } catch (err: Exception) {

            _errorHandler.value = ExceptionHandlerFactory.create(err)
        } finally {
            _loading.value = false
            _errorHandler.value = null
        }
    }
}

class MainViewModelFactory(private val overViewRefresh: suspend () -> Unit): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(overViewRefresh) as T
    }
}