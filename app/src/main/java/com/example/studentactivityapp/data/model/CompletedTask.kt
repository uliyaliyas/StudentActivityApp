package com.example.studentactivityapp.data.model

data class CompletedTask(
    val taskId: String = "",
    val title: String = "",
    val points: Int = 0,
    val completedAt: Long = 0L
)