package com.example.studentactivityapp.presentation.admin.studentprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AdminStudentProfileUiState(
    val isLoading: Boolean = false,
    val student: User? = null,
    val error: String? = null,
    val message: String? = null
)

class AdminStudentProfileViewModel : ViewModel() {

    private val firestore = FirebaseModule.firestore

    private val _uiState = MutableStateFlow(AdminStudentProfileUiState())
    val uiState: StateFlow<AdminStudentProfileUiState> = _uiState.asStateFlow()

    fun loadStudent(studentId: String) {
        viewModelScope.launch {
            _uiState.value = AdminStudentProfileUiState(isLoading = true)

            try {
                val doc = firestore.collection("users")
                    .document(studentId)
                    .get()
                    .await()

                val student = doc.toObject(User::class.java)

                _uiState.value = AdminStudentProfileUiState(student = student)
            } catch (e: Exception) {
                _uiState.value = AdminStudentProfileUiState(error = e.message)
            }
        }
    }

    fun addPoints(studentId: String, points: Int) {
        changePoints(studentId, points)
    }

    fun removePoints(studentId: String, points: Int) {
        changePoints(studentId, -points)
    }

    private fun changePoints(studentId: String, delta: Int) {
        viewModelScope.launch {
            try {
                val ref = firestore.collection("users").document(studentId)
                val snapshot = ref.get().await()
                val currentPoints = (snapshot.getLong("points") ?: 0).toInt()
                val newPoints = (currentPoints + delta).coerceAtLeast(0)

                ref.update("points", newPoints).await()

                _uiState.value = _uiState.value.copy(
                    message = "Баллы обновлены",
                    error = null
                )

                loadStudent(studentId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    message = null
                )
            }
        }
    }
}