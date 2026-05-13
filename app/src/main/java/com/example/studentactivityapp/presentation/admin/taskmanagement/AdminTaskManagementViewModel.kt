package com.example.studentactivityapp.presentation.admin.taskmanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.model.Task
import com.example.studentactivityapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TaskFormState(
    val taskId: String? = null,
    val title: String = "",
    val description: String = "",
    val pointsText: String = "",
    val deadline: Long = 0L,
    val isSaving: Boolean = false,
    val error: String? = null
) {
    val isEdit get() = taskId != null
    val isValid get() = title.isNotBlank() && description.isNotBlank() && pointsText.toIntOrNull() != null
}

data class AdminTasksUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val searchQuery: String = "",
    val showForm: Boolean = false,
    val form: TaskFormState = TaskFormState(),
    val error: String? = null
)

class AdminTaskManagementViewModel : ViewModel() {

    private val repository = TaskRepository()
    private var allTasks: List<Task> = emptyList()

    private val _uiState = MutableStateFlow(AdminTasksUiState())
    val uiState: StateFlow<AdminTasksUiState> = _uiState.asStateFlow()

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getAllTasks()
                .onSuccess { tasks ->
                    allTasks = tasks
                    applySearch(_uiState.value.searchQuery)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                }
        }
    }

    fun search(query: String) {
        applySearch(query)
    }

    private fun applySearch(query: String) {
        val filtered = if (query.isBlank()) allTasks
        else allTasks.filter { it.title.lowercase().contains(query.trim().lowercase()) }
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            tasks = filtered,
            searchQuery = query
        )
    }

    fun openAddForm() {
        _uiState.value = _uiState.value.copy(showForm = true, form = TaskFormState())
    }

    fun openEditForm(task: Task) {
        _uiState.value = _uiState.value.copy(
            showForm = true,
            form = TaskFormState(
                taskId = task.id,
                title = task.title,
                description = task.description,
                pointsText = task.points.toString(),
                deadline = task.deadline
            )
        )
    }

    fun updateDeadline(value: Long) {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(deadline = value))
    }

    fun clearDeadline() {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(deadline = 0L))
    }

    fun closeForm() {
        _uiState.value = _uiState.value.copy(showForm = false, form = TaskFormState())
    }

    fun updateTitle(value: String) {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(title = value, error = null))
    }

    fun updateDescription(value: String) {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(description = value, error = null))
    }

    fun updatePoints(value: String) {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(pointsText = value, error = null))
    }

    fun saveTask() {
        val form = _uiState.value.form
        val points = form.pointsText.toIntOrNull() ?: return
        _uiState.value = _uiState.value.copy(form = form.copy(isSaving = true, error = null))

        viewModelScope.launch {
            val result = if (form.isEdit) {
                repository.updateTask(form.taskId!!, form.title.trim(), form.description.trim(), points, form.deadline)
            } else {
                repository.createTask(form.title.trim(), form.description.trim(), points, form.deadline)
            }

            result.onSuccess {
                closeForm()
                loadTasks()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    form = _uiState.value.form.copy(isSaving = false, error = error.message ?: "Ошибка сохранения")
                )
            }
        }
    }

    fun deleteTask(taskId: String) {
        val snapshot = allTasks.toList()
        allTasks = allTasks.filter { it.id != taskId }
        applySearch(_uiState.value.searchQuery)

        viewModelScope.launch {
            repository.deleteTask(taskId).onFailure {
                allTasks = snapshot
                applySearch(_uiState.value.searchQuery)
                _uiState.value = _uiState.value.copy(error = it.message ?: "Ошибка удаления")
            }
        }
    }
}
