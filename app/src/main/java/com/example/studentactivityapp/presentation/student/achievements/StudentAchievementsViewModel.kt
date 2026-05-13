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
    val current: Int,
    val target: Int,
    val category: String
) {
    val progress: Float get() = (current.toFloat() / target).coerceIn(0f, 1f)
    val progressText: String get() = "$current / $target"
}

data class AchievementsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val achievements: List<AchievementItem> = emptyList(),
    val unlockedCount: Int = 0,
    val error: String? = null
)

class StudentAchievementsViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val taskRepository = TaskRepository()

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
            loadAchievementsInternal()
        }
    }

    fun loadAchievements() {
        viewModelScope.launch {
            _uiState.value = AchievementsUiState(isLoading = true)
            loadAchievementsInternal()
        }
    }

    private suspend fun loadAchievementsInternal() {

            val user = userRepository.getCurrentUser().getOrNull()
            val completedTasks = taskRepository.getCompletedTasks().getOrNull() ?: emptyList()
            val redemptions = userRepository.getRedemptionHistory().getOrNull() ?: emptyList()

            if (user == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, isRefreshing = false, error = "Не удалось загрузить данные")
                return
            }

            val completedCount = completedTasks.size
            val points = user.points
            val redemptionsCount = redemptions.size

            val achievements = listOf(
                AchievementItem(
                    title = "Первый шаг",
                    description = "Выполнить первое задание",
                    isUnlocked = completedCount >= 1,
                    current = completedCount.coerceAtMost(1),
                    target = 1,
                    category = "Задания"
                ),
                AchievementItem(
                    title = "Активный студент",
                    description = "Выполнить 5 заданий",
                    isUnlocked = completedCount >= 5,
                    current = completedCount.coerceAtMost(5),
                    target = 5,
                    category = "Задания"
                ),
                AchievementItem(
                    title = "Трудяга",
                    description = "Выполнить 10 заданий",
                    isUnlocked = completedCount >= 10,
                    current = completedCount.coerceAtMost(10),
                    target = 10,
                    category = "Задания"
                ),
                AchievementItem(
                    title = "Первые 100 баллов",
                    description = "Набрать 100 баллов",
                    isUnlocked = points >= 100,
                    current = points.coerceAtMost(100),
                    target = 100,
                    category = "Баллы"
                ),
                AchievementItem(
                    title = "Полпути",
                    description = "Набрать 500 баллов",
                    isUnlocked = points >= 500,
                    current = points.coerceAtMost(500),
                    target = 500,
                    category = "Баллы"
                ),
                AchievementItem(
                    title = "Лидер",
                    description = "Набрать 1000 баллов",
                    isUnlocked = points >= 1000,
                    current = points.coerceAtMost(1000),
                    target = 1000,
                    category = "Баллы"
                ),
                AchievementItem(
                    title = "Легенда",
                    description = "Набрать 2000 баллов",
                    isUnlocked = points >= 2000,
                    current = points.coerceAtMost(2000),
                    target = 2000,
                    category = "Баллы"
                ),
                AchievementItem(
                    title = "Шоппер",
                    description = "Получить первую награду",
                    isUnlocked = redemptionsCount >= 1,
                    current = redemptionsCount.coerceAtMost(1),
                    target = 1,
                    category = "Награды"
                ),
                AchievementItem(
                    title = "Коллекционер",
                    description = "Получить 3 награды",
                    isUnlocked = redemptionsCount >= 3,
                    current = redemptionsCount.coerceAtMost(3),
                    target = 3,
                    category = "Награды"
                )
            )

            _uiState.value = AchievementsUiState(
                achievements = achievements,
                unlockedCount = achievements.count { it.isUnlocked }
            )
    }
}
