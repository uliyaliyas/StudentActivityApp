package com.example.studentactivityapp.presentation.student.notifications

import androidx.lifecycle.ViewModel
import com.example.studentactivityapp.data.FirebaseModule
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val sentAt: Long
)

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val notifications: List<NotificationItem> = emptyList(),
    val error: String? = null
)

class StudentNotificationsViewModel : ViewModel() {

    private val firestore = FirebaseModule.firestore
    private var listener: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        startListening()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
        listener?.remove()
        attachListener()
    }

    private fun startListening() {
        _uiState.value = NotificationsUiState(isLoading = true)
        attachListener()
    }

    private fun attachListener() {
        listener = firestore.collection("notifications")
            .orderBy("sentAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, isRefreshing = false, error = error.message)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.map { doc ->
                    NotificationItem(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        body = doc.getString("body") ?: "",
                        sentAt = doc.getLong("sentAt") ?: 0L
                    )
                } ?: emptyList()
                _uiState.value = NotificationsUiState(notifications = items)
            }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}
