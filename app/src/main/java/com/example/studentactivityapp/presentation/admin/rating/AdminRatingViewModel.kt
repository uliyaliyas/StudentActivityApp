package com.example.studentactivityapp.presentation.admin.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.model.User
import com.example.studentactivityapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminRatingUiState(
    val isLoading: Boolean = false,
    val students: List<User> = emptyList(),
    val error: String? = null
)

class AdminRatingViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _uiState = MutableStateFlow(AdminRatingUiState())
    val uiState: StateFlow<AdminRatingUiState> = _uiState.asStateFlow()

    fun loadRating() {
        viewModelScope.launch {
            _uiState.value = AdminRatingUiState(isLoading = true)
            repository.getStudentsRating().onSuccess { students ->
                _uiState.value = AdminRatingUiState(students = students)
            }.onFailure { error ->
                _uiState.value = AdminRatingUiState(error = error.message ?: "Ошибка загрузки")
            }
        }
    }
}
