package com.example.studentactivityapp.presentation.student.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.model.RedemptionRecord
import com.example.studentactivityapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RedemptionHistoryUiState(
    val isLoading: Boolean = false,
    val records: List<RedemptionRecord> = emptyList(),
    val error: String? = null
)

class StudentRedemptionHistoryViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(RedemptionHistoryUiState())
    val uiState: StateFlow<RedemptionHistoryUiState> = _uiState.asStateFlow()

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = RedemptionHistoryUiState(isLoading = true)
            userRepository.getRedemptionHistory()
                .onSuccess { records ->
                    _uiState.value = RedemptionHistoryUiState(records = records)
                }
                .onFailure { error ->
                    _uiState.value = RedemptionHistoryUiState(error = error.message ?: "Ошибка загрузки")
                }
        }
    }
}
