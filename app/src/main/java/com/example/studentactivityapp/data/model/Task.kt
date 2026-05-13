package com.example.studentactivityapp.data.model

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val points: Int = 0,
    val isCompleted: Boolean = false,
    val deadline: Long = 0L
)