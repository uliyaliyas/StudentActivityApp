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
    val error: String? = null
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
}