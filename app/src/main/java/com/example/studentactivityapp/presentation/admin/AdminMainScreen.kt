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
import com.example.studentactivityapp.presentation.admin.addtask.AdminAddTaskScreen
import com.example.studentactivityapp.presentation.admin.home.AdminHomeScreen
import com.example.studentactivityapp.presentation.admin.rating.AdminRatingScreen
import com.example.studentactivityapp.presentation.admin.rewards.AdminRewardsScreen
import com.example.studentactivityapp.presentation.admin.statistics.AdminStatisticsScreen
import com.example.studentactivityapp.presentation.admin.studentprofile.AdminStudentProfileScreen
import com.example.studentactivityapp.presentation.admin.students.AdminStudentsScreen
import com.example.studentactivityapp.presentation.admin.taskmanagement.AdminTaskManagementScreen

@Composable
fun AdminMainScreen(
    onLogoutClick: () -> Unit
) {
    val navController = rememberNavController()

    val items = listOf(
        AdminBottomNavItem.Home,
        AdminBottomNavItem.Tasks,
        AdminBottomNavItem.Students,
        AdminBottomNavItem.Statistics,
        AdminBottomNavItem.Rating
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
                AdminHomeScreen(
                    innerPadding = innerPadding,
                    onLogoutClick = onLogoutClick,
                    onRewardsClick = {
                        navController.navigate(Screen.AdminRewards.route)
                    }
                )
            }

            composable(Screen.AdminTaskManagement.route) {
                AdminTaskManagementScreen(
                    innerPadding = innerPadding,
                    onAddTaskClick = {
                        navController.navigate(Screen.AdminAddTask.route)
                    }
                )
            }

            composable(Screen.AdminAddTask.route) {
                AdminAddTaskScreen(
                    innerPadding = innerPadding,
                    onTaskSaved = {
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.AdminStudents.route) {
                AdminStudentsScreen(
                    innerPadding = innerPadding,
                    onStudentClick = { studentId ->
                        navController.navigate(
                            Screen.AdminStudentProfile.createRoute(studentId)
                        )
                    }
                )
            }

            composable(Screen.AdminStudentProfile.route) { backStackEntry ->
                val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
                AdminStudentProfileScreen(
                    studentId = studentId,
                    innerPadding = innerPadding,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.AdminStatistics.route) {
                AdminStatisticsScreen(innerPadding = innerPadding)
            }

            composable(Screen.AdminRating.route) {
                AdminRatingScreen(innerPadding = innerPadding)
            }

            composable(Screen.AdminRewards.route) {
                AdminRewardsScreen(
                    innerPadding = innerPadding,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
