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
    val students: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class AdminRatingViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val _uiState = MutableStateFlow(AdminRatingUiState())
    val uiState: StateFlow<AdminRatingUiState> = _uiState.asStateFlow()

    fun loadRating() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            userRepository.getStudentsRating().fold(
                onSuccess = { students ->
                    _uiState.value = _uiState.value.copy(students = students, isLoading = false)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
                }
            )
        }
    }
}
