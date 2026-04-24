package com.example.studentactivityapp.presentation.student.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.repository.TaskRepository
import com.example.studentactivityapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AchievementItem(
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val progressText: String
)

data class AchievementsUiState(
    val achievements: List<AchievementItem> = emptyList(),
    val error: String? = null
)

class StudentAchievementsViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val taskRepository = TaskRepository()

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    fun loadAchievements() {
        viewModelScope.launch {
            val userResult = userRepository.getCurrentUser()
            val tasksResult = taskRepository.getCompletedTasks()

            val user = userResult.getOrNull()
            val completedTasks = tasksResult.getOrNull() ?: emptyList()

            if (user == null) {
                _uiState.value = AchievementsUiState(error = "Не удалось загрузить пользователя")
                return@launch
            }

            val completedCount = completedTasks.size
            val points = user.points

            _uiState.value = AchievementsUiState(
                achievements = listOf(
                    AchievementItem(
                        title = "Первый шаг",
                        description = "Выполнить первое задание",
                        isUnlocked = completedCount >= 1,
                        progressText = "$completedCount / 1"
                    ),
                    AchievementItem(
                        title = "Активный студент",
                        description = "Выполнить 5 заданий",
                        isUnlocked = completedCount >= 5,
                        progressText = "$completedCount / 5"
                    ),
                    AchievementItem(
                        title = "100 баллов",
                        description = "Набрать первые 100 баллов",
                        isUnlocked = points >= 100,
                        progressText = "$points / 100"
                    ),
                    AchievementItem(
                        title = "Активист",
                        description = "Набрать 500 баллов",
                        isUnlocked = points >= 500,
                        progressText = "$points / 500"
                    ),
                    AchievementItem(
                        title = "Лидер",
                        description = "Набрать 1000 баллов",
                        isUnlocked = points >= 1000,
                        progressText = "$points / 1000"
                    )
                )
            )
        }
    }
}