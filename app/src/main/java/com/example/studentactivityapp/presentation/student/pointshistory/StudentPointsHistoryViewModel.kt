package com.example.studentactivityapp.presentation.student.pointshistory

import androidx.lifecycle.ViewModel
import com.example.studentactivityapp.data.FirebaseModule
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class PointEventType { TASK, ADJUSTMENT }

data class PointHistoryItem(
    val id: String,
    val label: String,
    val delta: Int,
    val timestamp: Long,
    val type: PointEventType
)

data class PointsHistoryUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val items: List<PointHistoryItem> = emptyList(),
    val totalEarned: Int = 0,
    val totalSpent: Int = 0,
    val error: String? = null
)

class StudentPointsHistoryViewModel : ViewModel() {

    private val firestore = FirebaseModule.firestore
    private val auth = FirebaseModule.auth

    private var tasksListener: ListenerRegistration? = null
    private var adjustmentsListener: ListenerRegistration? = null

    private var taskEvents: List<PointHistoryItem> = emptyList()
    private var adjustmentEvents: List<PointHistoryItem> = emptyList()

    private val _uiState = MutableStateFlow(PointsHistoryUiState(isLoading = true))
    val uiState: StateFlow<PointsHistoryUiState> = _uiState.asStateFlow()

    init {
        attachListeners()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
        tasksListener?.remove()
        adjustmentsListener?.remove()
        attachListeners()
    }

    private fun attachListeners() {
        val uid = auth.currentUser?.uid ?: return

        tasksListener = firestore.collection("users").document(uid)
            .collection("completedTasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, isRefreshing = false, error = error.message)
                    return@addSnapshotListener
                }
                taskEvents = snapshot?.documents?.map { doc ->
                    PointHistoryItem(
                        id = "task_${doc.id}",
                        label = doc.getString("title") ?: "Задание",
                        delta = (doc.getLong("points") ?: 0).toInt(),
                        timestamp = doc.getLong("completedAt") ?: 0L,
                        type = PointEventType.TASK
                    )
                } ?: emptyList()
                recompute()
            }

        adjustmentsListener = firestore.collection("users").document(uid)
            .collection("pointAdjustments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                adjustmentEvents = snapshot?.documents?.map { doc ->
                    PointHistoryItem(
                        id = "adj_${doc.id}",
                        label = doc.getString("reason")?.takeIf { it.isNotBlank() }
                            ?: "Корректировка баллов",
                        delta = (doc.getLong("delta") ?: 0).toInt(),
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        type = PointEventType.ADJUSTMENT
                    )
                } ?: emptyList()
                recompute()
            }
    }

    private fun recompute() {
        val all = (taskEvents + adjustmentEvents).sortedByDescending { it.timestamp }
        val earned = all.filter { it.delta > 0 }.sumOf { it.delta }
        val spent = all.filter { it.delta < 0 }.sumOf { -it.delta }
        _uiState.value = PointsHistoryUiState(
            items = all,
            totalEarned = earned,
            totalSpent = spent,
            isRefreshing = false
        )
    }

    override fun onCleared() {
        super.onCleared()
        tasksListener?.remove()
        adjustmentsListener?.remove()
    }
}
