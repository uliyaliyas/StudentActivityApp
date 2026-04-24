package com.example.studentactivityapp.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val role: String = "student", // student / admin
    val name: String = "",
    val points: Int = 0
)