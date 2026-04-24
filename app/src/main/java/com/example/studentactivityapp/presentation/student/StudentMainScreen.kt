package com.example.studentactivityapp.presentation.student

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studentactivityapp.navigation.Screen
import com.example.studentactivityapp.navigation.StudentBottomNavItem
import com.example.studentactivityapp.presentation.student.achievements.StudentAchievementsScreen
import com.example.studentactivityapp.presentation.student.activity.StudentActivityScreen
import com.example.studentactivityapp.presentation.student.profile.StudentProfileScreen
import com.example.studentactivityapp.presentation.student.rating.StudentRatingScreen
import com.example.studentactivityapp.presentation.student.snake.StudentSnakeScreen
import com.example.studentactivityapp.presentation.student.tasks.StudentTasksScreen
import com.example.studentactivityapp.presentation.student.tasks.StudentTasksViewModel

@Composable
fun StudentMainScreen(
    onLogoutClick: () -> Unit
) {
    val navController = rememberNavController()

    val studentViewModel: StudentViewModel = viewModel()
    val tasksViewModel: StudentTasksViewModel = viewModel()

    LaunchedEffect(Unit) {
        tasksViewModel.onTaskCompleted = {
            studentViewModel.loadCurrentUser()
        }
    }

    val items = listOf(
        StudentBottomNavItem.Home,
        StudentBottomNavItem.Tasks,
        StudentBottomNavItem.Rating,
        StudentBottomNavItem.Snake,
        StudentBottomNavItem.Profile
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
            startDestination = Screen.StudentHome.route
        ) {
            composable(Screen.StudentHome.route) {
                StudentHomeScreen(
                    innerPadding = innerPadding,
                    viewModel = studentViewModel
                )
            }

            composable(Screen.StudentTasks.route) {
                StudentTasksScreen(
                    innerPadding = innerPadding,
                    viewModel = tasksViewModel
                )
            }

            composable(Screen.StudentRating.route) {
                StudentRatingScreen(innerPadding = innerPadding)
            }

            composable(Screen.StudentSnake.route) {
                StudentSnakeScreen(innerPadding = innerPadding)
            }

            composable(Screen.StudentProfile.route) {
                StudentProfileScreen(
                    innerPadding = innerPadding,
                    onLogoutClick = onLogoutClick,
                    onActivityClick = {
                        navController.navigate(Screen.StudentActivity.route)
                    },
                    onAchievementsClick = {
                        navController.navigate(Screen.StudentAchievements.route)
                    }
                )
            }

            composable(Screen.StudentActivity.route) {
                StudentActivityScreen(
                    innerPadding = innerPadding,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.StudentAchievements.route) {
                StudentAchievementsScreen(
                    innerPadding = innerPadding,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}