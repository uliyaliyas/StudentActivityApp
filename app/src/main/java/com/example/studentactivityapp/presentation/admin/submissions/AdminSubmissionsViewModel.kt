package com.example.studentactivityapp.presentation.admin.submissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.TaskSubmission
import com.example.studentactivityapp.data.repository.TaskSubmissionRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminSubmissionsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val submissions: List<TaskSubmission> = emptyList(),
    val error: String? = null
)

class AdminSubmissionsViewModel : ViewModel() {

    private val firestore = FirebaseModule.firestore
    private val repository = TaskSubmissionRepository()
    private var listener: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(AdminSubmissionsUiState())
    val uiState: StateFlow<AdminSubmissionsUiState> = _uiState.asStateFlow()

    init {
        startListening()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
        listener?.remove()
        attachListener()
    }

    private fun startListening() {
        _uiState.value = AdminSubmissionsUiState(isLoading = true)
        attachListener()
    }

    private fun attachListener() {
        listener = firestore.collection("taskSubmissions")
            .whereEqualTo("status", "pending")
            .orderBy("submittedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, isRefreshing = false, error = error.message)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.map { doc ->
                    TaskSubmission(
                        id = doc.id,
                        studentId = doc.getString("studentId") ?: "",
                        studentName = doc.getString("studentName") ?: "Студент",
                        taskId = doc.getString("taskId") ?: "",
                        taskTitle = doc.getString("taskTitle") ?: "",
                        points = (doc.getLong("points") ?: 0).toInt(),
                        submittedAt = doc.getLong("submittedAt") ?: 0L,
                        status = doc.getString("status") ?: "pending"
                    )
                } ?: emptyList()
                _uiState.value = AdminSubmissionsUiState(submissions = items)
            }
    }

    fun approve(submission: TaskSubmission) {
        viewModelScope.launch {
            repository.approveSubmission(submission)
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }

    fun reject(submission: TaskSubmission) {
        viewModelScope.launch {
            repository.rejectSubmission(submission.id)
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}
