package com.example.studentactivityapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.repository.AuthRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    fun register(
        email: String,
        password: String,
        name: String,
        role: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.register(
                email = email,
                password = password,
                name = name,
                role = role
            )

            result.onSuccess {
                if (role == "student") subscribeToStudentsTopic()
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Ошибка регистрации")
            }
        }
    }

    fun login(
        email: String,
        password: String,
        onStudentSuccess: () -> Unit,
        onAdminSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.login(email, password)

            result.onSuccess { user ->
                when (user.role) {
                    "admin" -> {
                        unsubscribeFromStudentsTopic()
                        onAdminSuccess()
                    }
                    "student" -> {
                        subscribeToStudentsTopic()
                        onStudentSuccess()
                    }
                    else -> onError("Неизвестная роль")
                }
            }.onFailure {
                onError(it.message ?: "Ошибка входа")
            }
        }
    }

    fun logout() {
        unsubscribeFromStudentsTopic()
        repository.logout()
    }

    fun getCurrentUserId(): String? {
        return repository.getCurrentUserId()
    }

    fun isUserLoggedIn(): Boolean {
        return repository.getCurrentUserId() != null
    }

    private fun subscribeToStudentsTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("students")
    }

    private fun unsubscribeFromStudentsTopic() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("students")
    }
}
