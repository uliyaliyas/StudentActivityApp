package com.example.studentactivityapp.presentation.admin.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.model.Reward
import com.example.studentactivityapp.data.repository.RewardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminRewardsUiState(
    val isLoading: Boolean = false,
    val rewards: List<Reward> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class AdminRewardsViewModel : ViewModel() {

    private val repository = RewardRepository()

    private val _uiState = MutableStateFlow(AdminRewardsUiState())
    val uiState: StateFlow<AdminRewardsUiState> = _uiState.asStateFlow()

    fun loadRewards() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getAllRewards().onSuccess { rewards ->
                _uiState.value = AdminRewardsUiState(rewards = rewards)
            }.onFailure { error ->
                _uiState.value = AdminRewardsUiState(error = error.message ?: "Ошибка загрузки")
            }
        }
    }

    fun addReward(title: String, description: String, points: Int) {
        if (title.isBlank() || points <= 0) return
        viewModelScope.launch {
            repository.createReward(title, description, points).onSuccess {
                _uiState.value = _uiState.value.copy(successMessage = "Награда добавлена")
                loadRewards()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(error = error.message ?: "Ошибка")
            }
        }
    }

    fun deleteReward(rewardId: String) {
        viewModelScope.launch {
            repository.deleteReward(rewardId).onSuccess {
                loadRewards()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(error = error.message ?: "Ошибка удаления")
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null, error = null)
    }
}
