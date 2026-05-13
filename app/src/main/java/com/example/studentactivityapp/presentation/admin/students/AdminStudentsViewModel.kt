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
    val searchQuery: String = "",
    val totalCount: Int = 0,
    val error: String? = null
)

class AdminStudentsViewModel : ViewModel() {

    private val firestore = FirebaseModule.firestore
    private var allStudents: List<User> = emptyList()

    private val _uiState = MutableStateFlow(AdminStudentsUiState())
    val uiState: StateFlow<AdminStudentsUiState> = _uiState.asStateFlow()

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = AdminStudentsUiState(isLoading = true)

            try {
                val snapshot = firestore.collection("users").get().await()

                allStudents = snapshot.documents.mapNotNull { doc ->
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

                applySearch(_uiState.value.searchQuery)

            } catch (e: Exception) {
                _uiState.value = AdminStudentsUiState(error = e.message)
            }
        }
    }

    fun search(query: String) {
        applySearch(query)
    }

    private fun applySearch(query: String) {
        val filtered = if (query.isBlank()) {
            allStudents
        } else {
            val q = query.trim().lowercase()
            allStudents.filter {
                it.name.lowercase().contains(q) || it.email.lowercase().contains(q)
            }
        }
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            students = filtered,
            searchQuery = query,
            totalCount = allStudents.size
        )
    }
}
