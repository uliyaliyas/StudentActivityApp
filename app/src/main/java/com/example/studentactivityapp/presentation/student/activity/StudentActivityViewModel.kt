package com.example.studentactivityapp.presentation.student.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.model.CompletedTask
import com.example.studentactivityapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentActivityUiState(
    val isLoading: Boolean = false,
    val completedTasks: List<CompletedTask> = emptyList(),
    val error: String? = null
)

class StudentActivityViewModel : ViewModel() {

    private val repository = TaskRepository()

    private val _uiState = MutableStateFlow(StudentActivityUiState())
    val uiState: StateFlow<StudentActivityUiState> = _uiState.asStateFlow()

    fun loadCompletedTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.getCompletedTasks()

            result.onSuccess { tasks ->
                _uiState.value = StudentActivityUiState(completedTasks = tasks)
            }.onFailure { error ->
                _uiState.value = StudentActivityUiState(error = error.message)
            }
        }
    }
}