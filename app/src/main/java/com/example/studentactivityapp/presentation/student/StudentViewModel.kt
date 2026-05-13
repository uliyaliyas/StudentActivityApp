package com.example.studentactivityapp.presentation.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.User
import com.example.studentactivityapp.data.repository.UserRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val nameUpdated: Boolean = false
)

class StudentViewModel : ViewModel() {

    private val repository = UserRepository()
    private val auth = FirebaseModule.auth
    private val firestore = FirebaseModule.firestore
    private var userListener: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(StudentUiState())
    val uiState: StateFlow<StudentUiState> = _uiState.asStateFlow()

    init {
        startListening()
    }

    private fun startListening() {
        val uid = auth.currentUser?.uid ?: return
        _uiState.value = StudentUiState(isLoading = true)
        userListener = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.value = StudentUiState(error = error.message)
                    return@addSnapshotListener
                }
                if (snapshot == null || !snapshot.exists()) return@addSnapshotListener
                val user = User(
                    uid = uid,
                    email = snapshot.getString("email") ?: "",
                    name = snapshot.getString("name") ?: "",
                    role = snapshot.getString("role") ?: "student",
                    points = (snapshot.getLong("points") ?: 0).toInt()
                )
                _uiState.value = _uiState.value.copy(isLoading = false, user = user)
            }
    }

    fun loadCurrentUser() { /* snapshot listener handles updates automatically */ }

    fun updateUserName(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.updateUserName(name).onSuccess {
                _uiState.value = _uiState.value.copy(nameUpdated = true)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(error = error.message ?: "Ошибка сохранения")
            }
        }
    }

    fun clearNameUpdated() {
        _uiState.value = _uiState.value.copy(nameUpdated = false)
    }

    override fun onCleared() {
        super.onCleared()
        userListener?.remove()
    }
}
