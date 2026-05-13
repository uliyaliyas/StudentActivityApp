package com.example.studentactivityapp.presentation.admin.students

import androidx.lifecycle.ViewModel
import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.User
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AdminStudentsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val students: List<User> = emptyList(),
    val searchQuery: String = "",
    val totalCount: Int = 0,
    val error: String? = null
)

class AdminStudentsViewModel : ViewModel() {

    private val firestore = FirebaseModule.firestore
    private var allStudents: List<User> = emptyList()
    private var studentsListener: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(AdminStudentsUiState())
    val uiState: StateFlow<AdminStudentsUiState> = _uiState.asStateFlow()

    init {
        startListening()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
        studentsListener?.remove()
        attachListener()
    }

    private fun startListening() {
        _uiState.value = AdminStudentsUiState(isLoading = true)
        attachListener()
    }

    private fun attachListener() {
        studentsListener = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, isRefreshing = false, error = error.message)
                    return@addSnapshotListener
                }
                allStudents = snapshot?.documents?.mapNotNull { doc ->
                    val role = doc.getString("role") ?: return@mapNotNull null
                    if (role != "student") return@mapNotNull null
                    User(
                        uid = doc.id,
                        email = doc.getString("email") ?: "",
                        name = doc.getString("name") ?: "",
                        role = role,
                        points = (doc.getLong("points") ?: 0).toInt()
                    )
                } ?: emptyList()
                applySearch(_uiState.value.searchQuery)
            }
    }

    fun loadStudents() { /* snapshot listener handles updates automatically */ }

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
            isRefreshing = false,
            students = filtered,
            searchQuery = query,
            totalCount = allStudents.size
        )
    }

    override fun onCleared() {
        super.onCleared()
        studentsListener?.remove()
    }
}
