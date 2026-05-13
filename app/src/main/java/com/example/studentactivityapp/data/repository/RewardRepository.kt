package com.example.studentactivityapp.data.repository

import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.Reward
import kotlinx.coroutines.tasks.await

class RewardRepository {

    private val firestore = FirebaseModule.firestore

    suspend fun getAllRewards(): Result<List<Reward>> {
        return try {
            val snapshot = firestore.collection("rewards").get().await()
            val rewards = snapshot.documents.map { doc ->
                Reward(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    points = (doc.getLong("points") ?: 0).toInt(),
                    iconName = doc.getString("iconName") ?: "gift"
                )
            }
            Result.success(rewards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createReward(
        title: String,
        description: String,
        points: Int,
        iconName: String = "gift"
    ): Result<Unit> {
        return try {
            val data = hashMapOf(
                "title" to title,
                "description" to description,
                "points" to points,
                "iconName" to iconName
            )
            firestore.collection("rewards").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateReward(rewardId: String, title: String, description: String, points: Int, iconName: String): Result<Unit> {
        return try {
            firestore.collection("rewards").document(rewardId).update(
                mapOf("title" to title, "description" to description, "points" to points, "iconName" to iconName)
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReward(rewardId: String): Result<Unit> {
        return try {
            firestore.collection("rewards").document(rewardId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
