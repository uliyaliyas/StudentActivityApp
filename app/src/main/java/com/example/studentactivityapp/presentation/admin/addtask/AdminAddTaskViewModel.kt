package com.example.studentactivityapp.presentation.admin.addtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.repository.TaskRepository
import kotlinx.coroutines.launch

class AdminAddTaskViewModel : ViewModel() {

    private val repository = TaskRepository()

    fun createTask(
        title: String,
        description: String,
        points: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.createTask(
                title = title,
                description = description,
                points = points
            )

            result.onSuccess {
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Ошибка создания задания")
            }
        }
    }
}