package com.example.studentactivityapp.data.repository

import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.User
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseModule.auth
    private val firestore = FirebaseModule.firestore

    suspend fun register(
        email: String,
        password: String,
        name: String,
        role: String = "student"
    ): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("UID null")

            val user = User(
                uid = uid,
                email = email,
                name = name,
                role = role,
                points = 0
            )

            firestore.collection("users")
                .document(uid)
                .set(user)
                .await()

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("UID null")

            val snapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            val user = snapshot.toObject(User::class.java)
                ?: throw Exception("User not found")

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}