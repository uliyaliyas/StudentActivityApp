package com.example.studentactivityapp.presentation.student.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.Task
import com.example.studentactivityapp.data.repository.TaskRepository
import com.example.studentactivityapp.data.repository.TaskSubmissionRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class TasksUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val pendingTaskIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val totalCount: Int = 0,
    val error: String? = null
)

class StudentTasksViewModel : ViewModel() {

    var onTaskCompleted: (() -> Unit)? = null
    private val repository = TaskRepository()
    private val submissionRepository = TaskSubmissionRepository()
    private val auth = FirebaseModule.auth
    private val firestore = FirebaseModule.firestore

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    private var tasksListener: ListenerRegistration? = null
    private var completedListener: ListenerRegistration? = null
    private var pendingListener: ListenerRegistration? = null
    private var activeTasks: List<Task> = emptyList()
    private var completedIds: Set<String> = emptySet()
    private var pendingTaskIds: Set<String> = emptySet()

    init {
        startListening()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
        tasksListener?.remove()
        completedListener?.remove()
        pendingListener?.remove()
        attachListeners()
    }

    private fun startListening() {
        _uiState.value = TasksUiState(isLoading = true)
        attachListeners()
    }

    private fun attachListeners() {
        val uid = auth.currentUser?.uid ?: return

        tasksListener = firestore.collection("tasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, isRefreshing = false, error = error.message)
                    return@addSnapshotListener
                }
                val all = snapshot?.documents?.map { doc ->
                    Task(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        points = (doc.getLong("points") ?: 0).toInt(),
                        deadline = doc.getLong("deadline") ?: 0L
                    )
                } ?: emptyList()
                activeTasks = all.filter { !completedIds.contains(it.id) }
                applySearch(_uiState.value.searchQuery)
            }

        completedListener = firestore.collection("users").document(uid)
            .collection("completedTasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                completedIds = snapshot?.documents?.map { it.id }?.toSet() ?: emptySet()
                activeTasks = activeTasks.filter { !completedIds.contains(it.id) }
                applySearch(_uiState.value.searchQuery)
            }

        pendingListener = firestore.collection("taskSubmissions")
            .whereEqualTo("studentId", uid)
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                pendingTaskIds = snapshot?.documents?.mapNotNull { it.getString("taskId") }?.toSet() ?: emptySet()
                applySearch(_uiState.value.searchQuery)
            }
    }

    private fun applySearch(query: String) {
        val filtered = if (query.isBlank()) activeTasks
        else activeTasks.filter { it.title.lowercase().contains(query.trim().lowercase()) }
        _uiState.value = TasksUiState(
            tasks = filtered,
            pendingTaskIds = pendingTaskIds,
            searchQuery = query,
            totalCount = activeTasks.size,
            isRefreshing = false
        )
    }

    fun search(query: String) {
        applySearch(query)
    }

    fun loadTasks() { /* snapshot listener handles updates automatically */ }

    fun submitTask(task: Task) {
        if (pendingTaskIds.contains(task.id)) return
        viewModelScope.launch {
            val user = FirebaseModule.firestore.collection("users")
                .document(FirebaseModule.auth.currentUser?.uid ?: return@launch)
                .get().await()
            val name = user.getString("name") ?: "Студент"
            submissionRepository.submitTask(task, name)
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Ошибка отправки заявки"
                    )
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tasksListener?.remove()
        completedListener?.remove()
        pendingListener?.remove()
    }
}
