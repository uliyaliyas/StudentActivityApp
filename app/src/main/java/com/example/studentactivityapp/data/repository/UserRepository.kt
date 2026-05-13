package com.example.studentactivityapp.data.repository

import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.RedemptionRecord
import com.example.studentactivityapp.data.model.Reward
import com.example.studentactivityapp.data.model.User
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserRepository {

    private val auth = FirebaseModule.auth
    private val firestore = FirebaseModule.firestore

    companion object {
        const val SNAKE_DAILY_LIMIT = 25
    }

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

    suspend fun updateUserName(name: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: throw Exception("Пользователь не авторизован")
            firestore.collection("users").document(uid).update("name", name).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deductPoints(amount: Int): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: throw Exception("Пользователь не авторизован")
            val userRef = firestore.collection("users").document(uid)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentPoints = (snapshot.getLong("points") ?: 0).toInt()
                if (currentPoints < amount) throw Exception("Недостаточно баллов")
                transaction.update(userRef, "points", currentPoints - amount)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSnakeDailyPoints(): Result<Int> {
        val uid = auth.currentUser?.uid ?: return Result.success(0)
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val savedDate = snapshot.getString("snakeDailyDate") ?: ""
            val daily = if (savedDate == today) {
                (snapshot.getLong("snakeDailyPoints") ?: 0).toInt()
            } else {
                0
            }
            Result.success(daily)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveRedemption(reward: Reward): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: throw Exception("Пользователь не авторизован")
            val record = hashMapOf(
                "rewardId" to reward.id,
                "rewardTitle" to reward.title,
                "rewardPoints" to reward.points,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("users").document(uid)
                .collection("redemptions")
                .add(record)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRedemptionHistory(): Result<List<RedemptionRecord>> {
        return try {
            val uid = auth.currentUser?.uid
                ?: throw Exception("Пользователь не авторизован")
            val snapshot = firestore.collection("users").document(uid)
                .collection("redemptions")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            val records = snapshot.documents.mapNotNull { doc ->
                RedemptionRecord(
                    id = doc.id,
                    rewardId = doc.getString("rewardId") ?: "",
                    rewardTitle = doc.getString("rewardTitle") ?: "",
                    rewardPoints = (doc.getLong("rewardPoints") ?: 0).toInt(),
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            }
            Result.success(records)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addSnakePoints(earnedPoints: Int): Result<Int> {
        val uid = auth.currentUser?.uid ?: return Result.success(0)
        return try {
            val userRef = firestore.collection("users").document(uid)
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            var added = 0
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val savedDate = snapshot.getString("snakeDailyDate") ?: ""
                val currentDaily = if (savedDate == today) {
                    (snapshot.getLong("snakeDailyPoints") ?: 0).toInt()
                } else {
                    0
                }
                val canAdd = minOf(earnedPoints, SNAKE_DAILY_LIMIT - currentDaily).coerceAtLeast(0)
                added = canAdd
                if (canAdd > 0) {
                    val currentPoints = (snapshot.getLong("points") ?: 0).toInt()
                    transaction.update(
                        userRef,
                        mapOf(
                            "points" to currentPoints + canAdd,
                            "snakeDailyPoints" to currentDaily + canAdd,
                            "snakeDailyDate" to today
                        )
                    )
                }
            }.await()
            Result.success(added)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
