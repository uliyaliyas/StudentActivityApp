package com.example.studentactivityapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studentactivityapp.presentation.admin.AdminMainScreen
import com.example.studentactivityapp.presentation.admin.addtask.AdminAddTaskScreen
import com.example.studentactivityapp.presentation.admin.rating.AdminRatingScreen
import com.example.studentactivityapp.presentation.admin.studentprofile.AdminStudentProfileScreen
import com.example.studentactivityapp.presentation.auth.AuthViewModel
import com.example.studentactivityapp.presentation.auth.LoginScreen
import com.example.studentactivityapp.presentation.auth.RegisterScreen
import com.example.studentactivityapp.presentation.auth.WelcomeScreen
import com.example.studentactivityapp.presentation.student.StudentMainScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel = AuthViewModel()

    val startDestination =
        if (authViewModel.isUserLoggedIn()) {
            Screen.StudentHome.route
        } else {
            Screen.Welcome.route
        }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                },
                onGuestClick = {
                    navController.navigate(Screen.StudentHome.route)
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onStudentSuccess = {
                    navController.navigate(Screen.StudentHome.route) {
                        popUpTo(Screen.Welcome.route) {
                            inclusive = true
                        }
                    }
                },
                onAdminSuccess = {
                    navController.navigate(Screen.AdminHome.route) {
                        popUpTo(Screen.Welcome.route) {
                            inclusive = true
                        }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.StudentHome.route) {
                        popUpTo(Screen.Welcome.route) {
                            inclusive = true
                        }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.StudentHome.route) {
            StudentMainScreen(
                onLogoutClick = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Screen.AdminHome.route) {
            AdminMainScreen()
        }

        composable(Screen.AdminAddTask.route) {
            AdminAddTaskScreen()
        }

        composable(Screen.AdminStudentProfile.route) {
            AdminStudentProfileScreen()
        }

        composable(Screen.AdminRating.route) {
            AdminRatingScreen()
        }
    }
}