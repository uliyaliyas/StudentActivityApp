package com.example.studentactivityapp.presentation.admin.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.repository.TaskRepository
import com.example.studentactivityapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class NotificationFormState(
    val title: String = "",
    val body: String = "",
    val isSending: Boolean = false,
    val error: String? = null
) {
    val isValid get() = title.isNotBlank() && body.isNotBlank()
}

data class AdminHomeUiState(
    val studentsCount: Int = 0,
    val tasksCount: Int = 0,
    val averagePoints: Int = 0,
    val topStudent: String = "Нет данных",
    val isLoading: Boolean = false,
    val error: String? = null,
    val showNotificationSheet: Boolean = false,
    val notificationForm: NotificationFormState = NotificationFormState(),
    val notificationSent: Boolean = false
)

class AdminHomeViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val taskRepository = TaskRepository()
    private val firestore = FirebaseModule.firestore

    private val _uiState = MutableStateFlow(AdminHomeUiState())
    val uiState: StateFlow<AdminHomeUiState> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val students = userRepository.getStudentsRating().getOrNull() ?: emptyList()
            val tasks = taskRepository.getAllTasks().getOrNull() ?: emptyList()
            val avg = if (students.isNotEmpty()) students.map { it.points }.average().toInt() else 0

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                studentsCount = students.size,
                tasksCount = tasks.size,
                averagePoints = avg,
                topStudent = students.firstOrNull()?.name ?: "Нет данных"
            )
        }
    }

    fun openNotificationSheet() {
        _uiState.value = _uiState.value.copy(
            showNotificationSheet = true,
            notificationForm = NotificationFormState(),
            notificationSent = false
        )
    }

    fun closeNotificationSheet() {
        _uiState.value = _uiState.value.copy(showNotificationSheet = false)
    }

    fun updateNotificationTitle(value: String) {
        _uiState.value = _uiState.value.copy(
            notificationForm = _uiState.value.notificationForm.copy(title = value, error = null)
        )
    }

    fun updateNotificationBody(value: String) {
        _uiState.value = _uiState.value.copy(
            notificationForm = _uiState.value.notificationForm.copy(body = value, error = null)
        )
    }

    fun sendNotification() {
        val form = _uiState.value.notificationForm
        _uiState.value = _uiState.value.copy(
            notificationForm = form.copy(isSending = true, error = null)
        )
        viewModelScope.launch {
            try {
                firestore.collection("notifications").add(
                    mapOf(
                        "title" to form.title.trim(),
                        "body" to form.body.trim(),
                        "sentAt" to System.currentTimeMillis(),
                        "adminId" to (FirebaseModule.auth.currentUser?.uid ?: "")
                    )
                ).await()
                _uiState.value = _uiState.value.copy(
                    notificationForm = NotificationFormState(),
                    notificationSent = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    notificationForm = form.copy(isSending = false, error = e.message ?: "Ошибка отправки")
                )
            }
        }
    }
}
