package com.example.studentactivityapp.data.repository

import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.CompletedTask
import com.example.studentactivityapp.data.model.Task
import kotlinx.coroutines.tasks.await

class TaskRepository {

    private val auth = FirebaseModule.auth
    private val firestore = FirebaseModule.firestore

    suspend fun getAllTasks(): Result<List<Task>> {
        return try {
            val snapshot = firestore.collection("tasks").get().await()

            val tasks = snapshot.documents.map { doc ->
                Task(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    points = (doc.getLong("points") ?: 0).toInt()
                )
            }

            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTask(
        title: String,
        description: String,
        points: Int
    ): Result<Unit> {
        return try {
            val task = hashMapOf(
                "title" to title,
                "description" to description,
                "points" to points
            )

            firestore.collection("tasks")
                .add(task)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTasks(): Result<List<Task>> {
        return try {
            val snapshot = firestore.collection("tasks").get().await()
            val uid = auth.currentUser?.uid ?: throw Exception("Пользователь не найден")

            val completedSnapshot = firestore.collection("users")
                .document(uid)
                .collection("completedTasks")
                .get()
                .await()

            val completedTaskIds = completedSnapshot.documents.map { it.id }.toSet()

            val tasks = snapshot.documents.map { doc ->
                Task(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    points = (doc.getLong("points") ?: 0).toInt(),
                    isCompleted = completedTaskIds.contains(doc.id)
                )
            }.filter { !it.isCompleted }

            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeTask(task: Task): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Пользователь не найден")

            val userRef = firestore.collection("users").document(uid)
            val completedTaskRef = userRef.collection("completedTasks").document(task.id)

            firestore.runTransaction { transaction ->
                val completedSnapshot = transaction.get(completedTaskRef)

                if (completedSnapshot.exists()) {
                    throw Exception("Задание уже выполнено")
                }

                val userSnapshot = transaction.get(userRef)
                val currentPoints = userSnapshot.getLong("points") ?: 0

                transaction.update(userRef, "points", currentPoints + task.points)

                transaction.set(
                    completedTaskRef,
                    mapOf(
                        "taskId" to task.id,
                        "title" to task.title,
                        "points" to task.points,
                        "completedAt" to System.currentTimeMillis()
                    )
                )
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCompletedTasks(): Result<List<CompletedTask>> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Пользователь не найден")

            val snapshot = firestore.collection("users")
                .document(uid)
                .collection("completedTasks")
                .get()
                .await()

            val tasks = snapshot.documents.map { doc ->
                CompletedTask(
                    taskId = doc.getString("taskId") ?: "",
                    title = doc.getString("title") ?: "",
                    points = (doc.getLong("points") ?: 0).toInt(),
                    completedAt = doc.getLong("completedAt") ?: 0L
                )
            }.sortedByDescending { it.completedAt }

            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}