package com.example.studentactivityapp.navigation

sealed class Screen(val route: String) {

    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Register : Screen("register")

    data object StudentHome : Screen("student_home")
    data object StudentTasks : Screen("student_tasks")
    data object StudentActivity : Screen("student_activity")
    data object StudentRating : Screen("student_rating")
    data object StudentAchievements : Screen("student_achievements")
    data object StudentRewards : Screen("student_rewards")
    data object StudentProfile : Screen("student_profile")
    data object StudentSnake : Screen("student_snake")

    data object AdminHome : Screen("admin_home")
    data object AdminTaskManagement : Screen("admin_task_management")
    data object AdminAddTask : Screen("admin_add_task")
    data object AdminStudents : Screen("admin_students")
    data object AdminStatistics : Screen("admin_statistics")
    data object AdminRating : Screen("admin_rating")

    data object AdminStudentProfile : Screen("admin_student_profile/{studentId}") {
        fun createRoute(studentId: String): String {
            return "admin_student_profile/$studentId"
        }
    }
}