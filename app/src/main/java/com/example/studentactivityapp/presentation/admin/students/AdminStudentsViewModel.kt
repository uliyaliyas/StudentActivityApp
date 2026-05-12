package com.example.studentactivityapp.presentation.admin.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AdminStudentsUiState(
    val isLoading: Boolean = false,
    val students: List<User> = emptyList(),
    val error: String? = null
)

class AdminStudentsViewModel : ViewModel() {

    private val firestore = FirebaseModule.firestore

    private val _uiState = MutableStateFlow(AdminStudentsUiState())
    val uiState: StateFlow<AdminStudentsUiState> = _uiState.asStateFlow()

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = AdminStudentsUiState(isLoading = true)

            try {
                val snapshot = firestore.collection("users").get().await()

                val students = snapshot.documents.mapNotNull { doc ->
                    val role = doc.getString("role") ?: return@mapNotNull null

                    if (role != "student") return@mapNotNull null

                    User(
                        uid = doc.id,
                        email = doc.getString("email") ?: "",
                        name = doc.getString("name") ?: "",
                        role = role,
                        points = (doc.getLong("points") ?: 0).toInt()
                    )
                }

                _uiState.value = AdminStudentsUiState(students = students)

            } catch (e: Exception) {
                _uiState.value = AdminStudentsUiState(error = e.message)
            }
        }
    }
}