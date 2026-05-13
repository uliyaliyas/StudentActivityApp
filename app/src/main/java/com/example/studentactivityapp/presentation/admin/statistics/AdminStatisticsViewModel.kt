package com.example.studentactivityapp.presentation.admin.statistics

import androidx.lifecycle.ViewModel
import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.User
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PointsBucket(val label: String, val count: Int)

data class AdminStatisticsUiState(
    val isLoading: Boolean = false,
    val studentsCount: Int = 0,
    val activeStudentsCount: Int = 0,
    val tasksCount: Int = 0,
    val rewardsCount: Int = 0,
    val totalPointsDistributed: Int = 0,
    val averagePoints: Int = 0,
    val topStudents: List<User> = emptyList(),
    val pointsBuckets: List<PointsBucket> = emptyList(),
    val error: String? = null
)

class AdminStatisticsViewModel : ViewModel() {

    private val firestore = FirebaseModule.firestore

    private var studentsListener: ListenerRegistration? = null
    private var tasksListener: ListenerRegistration? = null
    private var rewardsListener: ListenerRegistration? = null

    private var allStudents: List<User> = emptyList()
    private var tasksCount: Int = 0
    private var rewardsCount: Int = 0

    private val _uiState = MutableStateFlow(AdminStatisticsUiState(isLoading = true))
    val uiState: StateFlow<AdminStatisticsUiState> = _uiState.asStateFlow()

    init {
        startListening()
    }

    private fun startListening() {
        studentsListener = firestore.collection("users")
            .addSnapshotListener { snapshot, _ ->
                allStudents = snapshot?.documents?.mapNotNull { doc ->
                    if (doc.getString("role") != "student") return@mapNotNull null
                    User(
                        uid = doc.id,
                        email = doc.getString("email") ?: "",
                        name = doc.getString("name") ?: "",
                        role = "student",
                        points = (doc.getLong("points") ?: 0).toInt()
                    )
                } ?: emptyList()
                recompute()
            }

        tasksListener = firestore.collection("tasks")
            .addSnapshotListener { snapshot, _ ->
                tasksCount = snapshot?.size() ?: 0
                recompute()
            }

        rewardsListener = firestore.collection("rewards")
            .addSnapshotListener { snapshot, _ ->
                rewardsCount = snapshot?.size() ?: 0
                recompute()
            }
    }

    private fun recompute() {
        val students = allStudents
        val total = students.sumOf { it.points }
        val avg = if (students.isNotEmpty()) total / students.size else 0
        val active = students.count { it.points > 0 }
        val top = students.sortedByDescending { it.points }.take(5)
        val buckets = buildBuckets(students)

        _uiState.value = AdminStatisticsUiState(
            isLoading = false,
            studentsCount = students.size,
            activeStudentsCount = active,
            tasksCount = tasksCount,
            rewardsCount = rewardsCount,
            totalPointsDistributed = total,
            averagePoints = avg,
            topStudents = top,
            pointsBuckets = buckets
        )
    }

    private fun buildBuckets(students: List<User>): List<PointsBucket> {
        val ranges = listOf(
            "0" to (0..0),
            "1–99" to (1..99),
            "100–499" to (100..499),
            "500–999" to (500..999),
            "1000+" to (1000..Int.MAX_VALUE)
        )
        return ranges.map { (label, range) ->
            PointsBucket(label, students.count { it.points in range })
        }
    }

    fun loadStatistics() { /* snapshot listeners handle updates automatically */ }

    override fun onCleared() {
        super.onCleared()
        studentsListener?.remove()
        tasksListener?.remove()
        rewardsListener?.remove()
    }
}
