package com.example.studentactivityapp.data.model

data class RedemptionRecord(
    val id: String = "",
    val rewardId: String = "",
    val rewardTitle: String = "",
    val rewardPoints: Int = 0,
    val timestamp: Long = 0L
)
