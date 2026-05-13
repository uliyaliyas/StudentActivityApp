package com.example.studentactivityapp.presentation.admin.studentprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class PointAdjustmentRecord(
    val id: String = "",
    val delta: Int = 0,
    val reason: String = "",
    val timestamp: Long = 0L
)

data class AdjustmentFormState(
    val amount: String = "",
    val reason: String = "",
    val isAdding: Boolean = true,
    val isSaving: Boolean = false
) {
    val isValid get() = (amount.toIntOrNull() ?: 0) > 0
}

data class AdminStudentProfileUiState(
    val isLoading: Boolean = false,
    val student: User? = null,
    val adjustments: List<PointAdjustmentRecord> = emptyList(),
    val form: AdjustmentFormState = AdjustmentFormState(),
    val successMessage: String? = null,
    val error: String? = null
)

class AdminStudentProfileViewModel : ViewModel() {

    private val firestore = FirebaseModule.firestore
    private val auth = FirebaseModule.auth

    private val _uiState = MutableStateFlow(AdminStudentProfileUiState())
    val uiState: StateFlow<AdminStudentProfileUiState> = _uiState.asStateFlow()

    fun loadStudent(studentId: String) {
        viewModelScope.launch {
            _uiState.value = AdminStudentProfileUiState(isLoading = true)
            try {
                val doc = firestore.collection("users").document(studentId).get().await()
                val student = doc.toObject(User::class.java)
                _uiState.value = _uiState.value.copy(isLoading = false, student = student)
                loadAdjustments(studentId)
            } catch (e: Exception) {
                _uiState.value = AdminStudentProfileUiState(error = e.message)
            }
        }
    }

    private fun loadAdjustments(studentId: String) {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("users").document(studentId)
                    .collection("pointAdjustments")
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(20)
                    .get().await()
                val records = snapshot.documents.map { doc ->
                    PointAdjustmentRecord(
                        id = doc.id,
                        delta = (doc.getLong("delta") ?: 0).toInt(),
                        reason = doc.getString("reason") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }
                _uiState.value = _uiState.value.copy(adjustments = records)
            } catch (_: Exception) {}
        }
    }

    fun updateAmount(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(amount = value))
        }
    }

    fun updateReason(value: String) {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(reason = value))
    }

    fun setPreset(amount: Int) {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(amount = amount.toString()))
    }

    fun setMode(isAdding: Boolean) {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(isAdding = isAdding))
    }

    fun applyAdjustment(studentId: String) {
        val form = _uiState.value.form
        val amount = form.amount.toIntOrNull() ?: return
        val delta = if (form.isAdding) amount else -amount

        _uiState.value = _uiState.value.copy(form = form.copy(isSaving = true))

        viewModelScope.launch {
            try {
                val ref = firestore.collection("users").document(studentId)

                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(ref)
                    val current = (snapshot.getLong("points") ?: 0).toInt()
                    val newPoints = (current + delta).coerceAtLeast(0)
                    transaction.update(ref, "points", newPoints)
                }.await()

                val record = hashMapOf(
                    "delta" to delta,
                    "reason" to form.reason.trim(),
                    "adminId" to (auth.currentUser?.uid ?: ""),
                    "timestamp" to System.currentTimeMillis()
                )
                firestore.collection("users").document(studentId)
                    .collection("pointAdjustments")
                    .add(record).await()

                val label = if (form.isAdding) "начислено" else "списано"
                _uiState.value = _uiState.value.copy(
                    form = AdjustmentFormState(),
                    successMessage = "$amount баллов $label"
                )
                loadStudent(studentId)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    form = form.copy(isSaving = false),
                    error = e.message ?: "Ошибка"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null, error = null)
    }
}
