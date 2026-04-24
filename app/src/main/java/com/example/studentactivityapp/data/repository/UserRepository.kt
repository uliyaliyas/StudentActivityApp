package com.example.studentactivityapp.data.repository

import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.User
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth = FirebaseModule.auth
    private val firestore = FirebaseModule.firestore

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val uid = auth.currentUser?.uid
                ?: throw Exception("Пользователь не авторизован")

            val snapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            val user = snapshot.toObject(User::class.java)
                ?: throw Exception("Данные пользователя не найдены")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStudentsRating(): Result<List<User>> {
        return try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("role", "student")
                .get()
                .await()

            val students = snapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }.sortedByDescending { it.points }

            Result.success(students)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}