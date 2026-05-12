package com.example.studentactivityapp.data.repository

import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.User
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserRepository {

    companion object {
        const val SNAKE_DAILY_LIMIT = 25
    }

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

    suspend fun getSnakeDailyPoints(): Result<Int> {
        return try {
            val uid = auth.currentUser?.uid
                ?: throw Exception("Пользователь не авторизован")

            val snapshot = firestore.collection("users").document(uid).get().await()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val storedDate = snapshot.getString("snakeDailyDate") ?: ""
            val dailyPoints = if (storedDate == today) {
                (snapshot.getLong("snakeDailyPoints") ?: 0).toInt()
            } else {
                0
            }
            Result.success(dailyPoints)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addSnakePoints(earnedPoints: Int): Result<Int> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.success(0)

            val userRef = firestore.collection("users").document(uid)
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            var actuallyAdded = 0

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val storedDate = snapshot.getString("snakeDailyDate") ?: ""
                val dailyPoints = if (storedDate == today) {
                    (snapshot.getLong("snakeDailyPoints") ?: 0).toInt()
                } else {
                    0
                }

                val canAdd = (SNAKE_DAILY_LIMIT - dailyPoints).coerceAtLeast(0)
                actuallyAdded = earnedPoints.coerceAtMost(canAdd)

                if (actuallyAdded > 0) {
                    val currentTotal = (snapshot.getLong("points") ?: 0).toInt()
                    transaction.update(
                        userRef,
                        mapOf(
                            "points" to currentTotal + actuallyAdded,
                            "snakeDailyPoints" to dailyPoints + actuallyAdded,
                            "snakeDailyDate" to today
                        )
                    )
                }
            }.await()

            Result.success(actuallyAdded)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
