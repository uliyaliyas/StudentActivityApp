package com.example.studentactivityapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.repository.AuthRepository
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
                    "admin" -> onAdminSuccess()
                    "student" -> onStudentSuccess()
                    else -> onError("Неизвестная роль")
                }
            }.onFailure {
                onError(it.message ?: "Ошибка входа")
            }
        }
    }

    fun logout() {
        repository.logout()
    }

    fun getCurrentUserId(): String? {
        return repository.getCurrentUserId()
    }
    fun isUserLoggedIn(): Boolean {
        return repository.getCurrentUserId() != null
    }
}