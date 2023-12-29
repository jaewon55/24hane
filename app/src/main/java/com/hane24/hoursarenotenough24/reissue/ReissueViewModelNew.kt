package com.hane24.hoursarenotenough24.reissue

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.repository.ReissueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

enum class ReissueState {
    NONE,
    APPLY,
    IN_PROGRESS,
    PICK_UP_REQUESTED,
    DONE,
}

class ReissueViewModelNew(
    private val reissueRepository: ReissueRepository
) : ViewModel() {

    var reissueState by mutableStateOf(ReissueState.NONE)
        private set

    private val _loadingState = MutableStateFlow(true)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    private val _errorSate = MutableStateFlow(State.SUCCESS)
    val errorState: StateFlow<State>
        get() = _errorSate

    init {
        viewModelScope.launch {
            _loadingState.value = true
            getReissueState()
            _loadingState.value = false
        }
    }

    suspend fun reload() {
        _loadingState.value = true
        getReissueState()
        _loadingState.value = false
    }

    fun reissueApply() {
        viewModelScope.launch {
            _loadingState.value = true
            postReissueRequest()
            getReissueState()
            _loadingState.value = false
        }
    }

    fun reissueFinish() {
        viewModelScope.launch {
            _loadingState.value = true
            patchReissueFinish()
            getReissueState()
            _loadingState.value = false
        }
    }

    private suspend fun getReissueState() {
        try {
            val state = reissueRepository.getState().state
            reissueState = when (state) {
                "none" -> ReissueState.NONE
                "apply" -> ReissueState.APPLY
                "in_progress" -> ReissueState.IN_PROGRESS
                "pick_up_requested" -> ReissueState.PICK_UP_REQUESTED
                "done" -> ReissueState.DONE
                else -> throw Exception("network error")
            }
        } catch (e: Exception) {
            _errorSate.value = State.NETWORK_FAIL
        } finally {
            _errorSate.value = State.SUCCESS
        }
    }

    private suspend fun postReissueRequest() {
        try {
            reissueRepository.reissue()
            getReissueState()
        } catch (error: HttpException) {
            when (error.code()) {
                404, 503 -> _errorSate.value = State.SERVER_FAIL
            }
        } catch (e: Exception) {
            _errorSate.value = State.NETWORK_FAIL
        } finally {
            _errorSate.value = State.SUCCESS
        }
    }

    private suspend fun patchReissueFinish() {
        try {
            reissueRepository.finish()
            getReissueState()
        } catch (error: HttpException) {
            when (error.code()) {
                404, 503 -> _errorSate.value = State.SERVER_FAIL
            }
        } catch (e: Exception) {
            _errorSate.value = State.NETWORK_FAIL
        } finally {
            _errorSate.value = State.SUCCESS
        }
    }

}

class ReissueViewModelFactory(
    private val reissueRepository: ReissueRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReissueViewModelNew(reissueRepository) as T
    }
}
