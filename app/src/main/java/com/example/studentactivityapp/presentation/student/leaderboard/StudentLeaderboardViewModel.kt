package com.example.studentactivityapp.presentation.student.leaderboard

import androidx.lifecycle.ViewModel
import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.User
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LeaderboardEntry(
    val rank: Int,
    val user: User,
    val isCurrentUser: Boolean
)

data class LeaderboardUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val entries: List<LeaderboardEntry> = emptyList(),
    val currentUserRank: Int? = null,
    val error: String? = null
)

class StudentLeaderboardViewModel : ViewModel() {

    private val firestore = FirebaseModule.firestore
    private val auth = FirebaseModule.auth
    private var listener: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        startListening()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
        listener?.remove()
        attachListener()
    }

    private fun startListening() {
        _uiState.value = LeaderboardUiState(isLoading = true)
        attachListener()
    }

    private fun attachListener() {
        val currentUid = auth.currentUser?.uid
        listener = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, isRefreshing = false, error = error.message)
                    return@addSnapshotListener
                }
                val students = snapshot?.documents?.mapNotNull { doc ->
                    if (doc.getString("role") != "student") return@mapNotNull null
                    User(
                        uid = doc.id,
                        email = doc.getString("email") ?: "",
                        name = doc.getString("name") ?: "",
                        role = "student",
                        points = (doc.getLong("points") ?: 0).toInt()
                    )
                }?.sortedByDescending { it.points } ?: emptyList()

                val entries = students.mapIndexed { index, user ->
                    LeaderboardEntry(
                        rank = index + 1,
                        user = user,
                        isCurrentUser = user.uid == currentUid
                    )
                }
                val currentUserRank = entries.firstOrNull { it.isCurrentUser }?.rank

                _uiState.value = LeaderboardUiState(
                    entries = entries,
                    currentUserRank = currentUserRank
                )
            }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}
