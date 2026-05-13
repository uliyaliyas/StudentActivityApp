package com.example.studentactivityapp.data.model

data class TaskSubmission(
    val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val taskId: String = "",
    val taskTitle: String = "",
    val points: Int = 0,
    val submittedAt: Long = 0L,
    val status: String = "pending"
)
