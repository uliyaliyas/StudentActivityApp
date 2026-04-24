package com.example.studentactivityapp.presentation.student.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.model.Task
import com.example.studentactivityapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TasksUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val error: String? = null
)

class StudentTasksViewModel : ViewModel() {

    var onTaskCompleted: (() -> Unit)? = null
    private val repository = TaskRepository()

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.getTasks()

            result.onSuccess { tasks ->
                _uiState.value = TasksUiState(tasks = tasks)
            }.onFailure { error ->
                _uiState.value = TasksUiState(error = error.message)
            }
        }
    }

    fun completeTask(task: Task) {
        if (task.isCompleted) return

        viewModelScope.launch {
            val result = repository.completeTask(task)

            result.onSuccess {
                loadTasks()
                onTaskCompleted?.invoke()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "Ошибка выполнения задания"
                )
            }
        }
    }
}