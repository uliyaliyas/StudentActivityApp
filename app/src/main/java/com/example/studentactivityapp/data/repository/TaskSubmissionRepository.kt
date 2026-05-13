package com.example.studentactivityapp.data.repository

import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.Task
import com.example.studentactivityapp.data.model.TaskSubmission
import kotlinx.coroutines.tasks.await

class TaskSubmissionRepository {

    private val auth = FirebaseModule.auth
    private val firestore = FirebaseModule.firestore

    suspend fun submitTask(task: Task, studentName: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Пользователь не найден")

            val existing = firestore.collection("taskSubmissions")
                .whereEqualTo("studentId", uid)
                .whereEqualTo("taskId", task.id)
                .whereEqualTo("status", "pending")
                .get().await()

            if (!existing.isEmpty) throw Exception("Заявка уже отправлена")

            firestore.collection("taskSubmissions").add(
                mapOf(
                    "studentId" to uid,
                    "studentName" to studentName,
                    "taskId" to task.id,
                    "taskTitle" to task.title,
                    "points" to task.points,
                    "submittedAt" to System.currentTimeMillis(),
                    "status" to "pending"
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun approveSubmission(submission: TaskSubmission): Result<Unit> {
        return try {
            val userRef = firestore.collection("users").document(submission.studentId)
            val completedTaskRef = userRef.collection("completedTasks").document(submission.taskId)
            val submissionRef = firestore.collection("taskSubmissions").document(submission.id)

            firestore.runTransaction { transaction ->
                val userSnap = transaction.get(userRef)
                val currentPoints = userSnap.getLong("points") ?: 0
                transaction.update(userRef, "points", currentPoints + submission.points)
                transaction.set(
                    completedTaskRef,
                    mapOf(
                        "taskId" to submission.taskId,
                        "title" to submission.taskTitle,
                        "points" to submission.points,
                        "completedAt" to System.currentTimeMillis()
                    )
                )
                transaction.update(submissionRef, "status", "approved")
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectSubmission(submissionId: String): Result<Unit> {
        return try {
            firestore.collection("taskSubmissions").document(submissionId)
                .update("status", "rejected").await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
