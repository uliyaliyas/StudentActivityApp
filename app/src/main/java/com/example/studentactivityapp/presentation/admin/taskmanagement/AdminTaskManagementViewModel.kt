package com.example.studentactivityapp.presentation.admin.taskmanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.model.Task
import com.example.studentactivityapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminTasksUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val error: String? = null
)

class AdminTaskManagementViewModel : ViewModel() {

    private val repository = TaskRepository()

    private val _uiState = MutableStateFlow(AdminTasksUiState())
    val uiState: StateFlow<AdminTasksUiState> = _uiState.asStateFlow()

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.getAllTasks()

            result.onSuccess { tasks ->
                _uiState.value = AdminTasksUiState(tasks = tasks)
            }.onFailure { error ->
                _uiState.value = AdminTasksUiState(error = error.message)
            }
        }
    }
}