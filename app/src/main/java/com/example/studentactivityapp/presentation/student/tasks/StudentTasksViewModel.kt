package com.example.studentactivityapp.presentation.student.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.Task
import com.example.studentactivityapp.data.repository.TaskRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TasksUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val error: String? = null
)

class StudentTasksViewModel : ViewModel() {

    var onTaskCompleted: (() -> Unit)? = null
    private val repository = TaskRepository()
    private val auth = FirebaseModule.auth
    private val firestore = FirebaseModule.firestore

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    private var tasksListener: ListenerRegistration? = null
    private var completedListener: ListenerRegistration? = null
    private var allTasks: List<Task> = emptyList()
    private var completedIds: Set<String> = emptySet()

    init {
        startListening()
    }

    private fun startListening() {
        val uid = auth.currentUser?.uid ?: return
        _uiState.value = TasksUiState(isLoading = true)

        tasksListener = firestore.collection("tasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.value = TasksUiState(error = error.message)
                    return@addSnapshotListener
                }
                allTasks = snapshot?.documents?.map { doc ->
                    Task(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        points = (doc.getLong("points") ?: 0).toInt(),
                        deadline = doc.getLong("deadline") ?: 0L
                    )
                } ?: emptyList()
                updateTaskList()
            }

        completedListener = firestore.collection("users").document(uid)
            .collection("completedTasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                completedIds = snapshot?.documents?.map { it.id }?.toSet() ?: emptySet()
                updateTaskList()
            }
    }

    private fun updateTaskList() {
        val tasks = allTasks
            .map { it.copy(isCompleted = completedIds.contains(it.id)) }
            .filter { !it.isCompleted }
        _uiState.value = TasksUiState(tasks = tasks)
    }

    fun loadTasks() { /* snapshot listener handles updates automatically */ }

    fun completeTask(task: Task) {
        if (task.isCompleted) return
        viewModelScope.launch {
            repository.completeTask(task)
                .onSuccess { onTaskCompleted?.invoke() }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Ошибка выполнения задания"
                    )
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tasksListener?.remove()
        completedListener?.remove()
    }
}
