package com.example.studentactivityapp.presentation.student.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.model.User
import com.example.studentactivityapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentRatingUiState(
    val isLoading: Boolean = false,
    val students: List<User> = emptyList(),
    val error: String? = null
)

class StudentRatingViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _uiState = MutableStateFlow(StudentRatingUiState())
    val uiState: StateFlow<StudentRatingUiState> = _uiState.asStateFlow()

    fun loadRating() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.getStudentsRating()

            result.onSuccess { students ->
                _uiState.value = StudentRatingUiState(students = students)
            }.onFailure { error ->
                _uiState.value = StudentRatingUiState(error = error.message)
            }
        }
    }
}