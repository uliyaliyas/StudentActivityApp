package com.example.studentactivityapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.ui.graphics.vector.ImageVector

sealed class StudentBottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : StudentBottomNavItem(
        route = Screen.StudentHome.route,
        title = "Главная",
        icon = Icons.Default.Home
    )

    data object Tasks : StudentBottomNavItem(
        route = Screen.StudentTasks.route,
        title = "Задания",
        icon = Icons.Default.List
    )

    data object Snake : StudentBottomNavItem(
        route = Screen.StudentSnake.route,
        title = "Игра",
        icon = Icons.Default.SportsEsports
    )

    data object Rewards : StudentBottomNavItem(
        route = Screen.StudentRewards.route,
        title = "Награды",
        icon = Icons.Default.CardGiftcard
    )

    data object Profile : StudentBottomNavItem(
        route = Screen.StudentProfile.route,
        title = "Профиль",
        icon = Icons.Default.Person
    )
}