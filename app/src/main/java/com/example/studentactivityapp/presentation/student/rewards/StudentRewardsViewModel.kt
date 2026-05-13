package com.example.studentactivityapp.presentation.student.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.model.Reward
import com.example.studentactivityapp.data.repository.RewardRepository
import com.example.studentactivityapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentRewardsUiState(
    val isLoading: Boolean = false,
    val rewards: List<Reward> = emptyList(),
    val userPoints: Int = 0,
    val error: String? = null,
    val successMessage: String? = null
)

class StudentRewardsViewModel : ViewModel() {

    private val rewardRepository = RewardRepository()
    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(StudentRewardsUiState())
    val uiState: StateFlow<StudentRewardsUiState> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = StudentRewardsUiState(isLoading = true)

            val rewards = rewardRepository.getAllRewards().getOrNull() ?: emptyList()
            val user = userRepository.getCurrentUser().getOrNull()

            _uiState.value = StudentRewardsUiState(
                rewards = rewards,
                userPoints = user?.points ?: 0
            )
        }
    }

    fun redeemReward(reward: Reward) {
        val currentPoints = _uiState.value.userPoints
        if (currentPoints < reward.points) {
            _uiState.value = _uiState.value.copy(error = "Недостаточно баллов")
            return
        }
        viewModelScope.launch {
            userRepository.deductPoints(reward.points).onSuccess {
                userRepository.saveRedemption(reward)
                _uiState.value = _uiState.value.copy(
                    userPoints = currentPoints - reward.points,
                    successMessage = "Награда «${reward.title}» получена!"
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(error = error.message ?: "Ошибка")
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null, error = null)
    }
}
