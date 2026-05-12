package com.example.studentactivityapp.presentation.admin.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.repository.TaskRepository
import com.example.studentactivityapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminHomeUiState(
    val studentsCount: Int = 0,
    val tasksCount: Int = 0,
    val averagePoints: Int = 0,
    val topStudent: String = "Нет данных",
    val isLoading: Boolean = false,
    val error: String? = null
)

class AdminHomeViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val taskRepository = TaskRepository()

    private val _uiState = MutableStateFlow(AdminHomeUiState())
    val uiState: StateFlow<AdminHomeUiState> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val studentsResult = userRepository.getStudentsRating()
            val tasksResult = taskRepository.getAllTasks()

            val students = studentsResult.getOrNull() ?: emptyList()
            val tasks = tasksResult.getOrNull() ?: emptyList()

            val avg = if (students.isNotEmpty()) {
                students.map { it.points }.average().toInt()
            } else {
                0
            }

            _uiState.value = AdminHomeUiState(
                studentsCount = students.size,
                tasksCount = tasks.size,
                averagePoints = avg,
                topStudent = students.firstOrNull()?.name ?: "Нет данных"
            )
        }
    }
}