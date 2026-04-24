package com.example.studentactivityapp.presentation.admin

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studentactivityapp.navigation.AdminBottomNavItem
import com.example.studentactivityapp.navigation.Screen
import com.example.studentactivityapp.presentation.admin.home.AdminHomeScreen
import com.example.studentactivityapp.presentation.admin.statistics.AdminStatisticsScreen
import com.example.studentactivityapp.presentation.admin.students.AdminStudentsScreen
import com.example.studentactivityapp.presentation.admin.taskmanagement.AdminTaskManagementScreen

@Composable
fun AdminMainScreen() {
    val navController = rememberNavController()

    val items = listOf(
        AdminBottomNavItem.Home,
        AdminBottomNavItem.Tasks,
        AdminBottomNavItem.Students,
        AdminBottomNavItem.Statistics
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry.value?.destination

                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(text = item.title)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.AdminHome.route
        ) {
            composable(Screen.AdminHome.route) {
                AdminHomeScreen(innerPadding = innerPadding)
            }
            composable(Screen.AdminTaskManagement.route) {
                AdminTaskManagementScreen(innerPadding = innerPadding)
            }
            composable(Screen.AdminStudents.route) {
                AdminStudentsScreen(innerPadding = innerPadding)
            }
            composable(Screen.AdminStatistics.route) {
                AdminStatisticsScreen(innerPadding = innerPadding)
            }
        }
    }
}