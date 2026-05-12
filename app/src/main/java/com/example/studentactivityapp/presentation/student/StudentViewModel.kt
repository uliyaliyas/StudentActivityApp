package com.example.studentactivityapp.presentation.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.model.User
import com.example.studentactivityapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val nameUpdated: Boolean = false
)

class StudentViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _uiState = MutableStateFlow(StudentUiState())
    val uiState: StateFlow<StudentUiState> = _uiState.asStateFlow()

    fun loadCurrentUser() {
        viewModelScope.launch {
            _uiState.value = StudentUiState(isLoading = true)

            val result = repository.getCurrentUser()

            result.onSuccess { user ->
                _uiState.value = StudentUiState(user = user)
            }.onFailure { error ->
                _uiState.value = StudentUiState(error = error.message ?: "Ошибка загрузки")
            }
        }
    }

    fun updateUserName(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.updateUserName(name).onSuccess {
                val updatedUser = _uiState.value.user?.copy(name = name)
                _uiState.value = _uiState.value.copy(user = updatedUser, nameUpdated = true)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(error = error.message ?: "Ошибка сохранения")
            }
        }
    }

    fun clearNameUpdated() {
        _uiState.value = _uiState.value.copy(nameUpdated = false)
    }
}
