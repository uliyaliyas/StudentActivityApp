package com.example.studentactivityapp.presentation.admin.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.model.User
import com.example.studentactivityapp.data.repository.TaskRepository
import com.example.studentactivityapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminStatisticsUiState(
    val isLoading: Boolean = false,
    val studentsCount: Int = 0,
    val tasksCount: Int = 0,
    val averagePoints: Int = 0,
    val topStudents: List<User> = emptyList(),
    val error: String? = null
)

class AdminStatisticsViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val taskRepository = TaskRepository()

    private val _uiState = MutableStateFlow(AdminStatisticsUiState())
    val uiState: StateFlow<AdminStatisticsUiState> = _uiState.asStateFlow()

    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val studentsResult = userRepository.getStudentsRating()
            val tasksResult = taskRepository.getAllTasks()

            studentsResult.fold(
                onSuccess = { students ->
                    val avg = if (students.isNotEmpty()) students.sumOf { it.points } / students.size else 0
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        studentsCount = students.size,
                        tasksCount = tasksResult.getOrNull()?.size ?: 0,
                        averagePoints = avg,
                        topStudents = students.take(5)
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
                }
            )
        }
    }
}
