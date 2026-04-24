package com.example.studentactivityapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AdminBottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : AdminBottomNavItem(
        route = Screen.AdminHome.route,
        title = "Главная",
        icon = Icons.Filled.Home
    )

    data object Tasks : AdminBottomNavItem(
        route = Screen.AdminTaskManagement.route,
        title = "Задания",
        icon = Icons.AutoMirrored.Filled.List
    )

    data object Students : AdminBottomNavItem(
        route = Screen.AdminStudents.route,
        title = "Студенты",
        icon = Icons.Filled.People
    )

    data object Statistics : AdminBottomNavItem(
        route = Screen.AdminStatistics.route,
        title = "Статистика",
        icon = Icons.Filled.BarChart
    )
}