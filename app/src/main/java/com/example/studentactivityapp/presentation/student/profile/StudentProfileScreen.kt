package com.example.studentactivityapp.presentation.student.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import com.example.studentactivityapp.ui.components.InitialsAvatar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentactivityapp.presentation.auth.AuthViewModel
import com.example.studentactivityapp.presentation.student.StudentViewModel

@Composable
fun StudentProfileScreen(
    innerPadding: PaddingValues,
    onLogoutClick: () -> Unit,
    onActivityClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    viewModel: StudentViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    val nameUpdated = uiState.nameUpdated
    LaunchedEffect(nameUpdated) {
        if (nameUpdated) {
            viewModel.clearNameUpdated()
        }
    }

    val user = uiState.user
    val name = user?.name ?: "Студент"
    val email = user?.email ?: "email не указан"
    val points = user?.points ?: 0
    val level = getStudentLevel(points)

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF7F3FF), Color.White)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Профиль",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InitialsAvatar(name = name, size = 90.dp, fontSize = 36.sp)

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1B69)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Изменить имя",
                            tint = Color(0xFF7B61FF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color(0xFF7A6F9B),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(text = email, color = Color(0xFF7A6F9B))
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat("Баллы", points.toString())
                    ProfileStat("Звание", level)
                    ProfileStat("Роль", "Студент")
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        InfoCard(
            title = "Текущий уровень",
            value = level,
            subtitle = getLevelDescription(points),
            icon = Icons.Default.EmojiEvents
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoCard(
            title = "Баланс баллов",
            value = points.toString(),
            subtitle = "Баллы начисляются за задания и активность",
            icon = Icons.Default.Star
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onActivityClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B61FF))
        ) {
            Icon(imageVector = Icons.Default.History, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Моя активность")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onAchievementsClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C7BFF))
        ) {
            Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Мои достижения")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onNotificationsClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0))
        ) {
            Icon(imageVector = Icons.Default.Notifications, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Уведомления")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                authViewModel.logout()
                onLogoutClick()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
        ) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Выйти из аккаунта")
        }

        val currentError = uiState.error
        if (currentError != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = currentError, color = Color.Red)
        }
    }

    if (showEditDialog) {
        EditNameDialog(
            currentName = name,
            onDismiss = { showEditDialog = false },
            onConfirm = { newName ->
                viewModel.updateUserName(newName)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var nameText by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Изменить имя",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1B69)
            )
        },
        text = {
            OutlinedTextField(
                value = nameText,
                onValueChange = { nameText = it },
                label = { Text("Имя") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(nameText) },
                enabled = nameText.isNotBlank()
            ) {
                Text("Сохранить", color = Color(0xFF7B61FF))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun ProfileStat(
    title: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF8A84A0)
        )
    }
}

@Composable
private fun InfoCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color(0xFFEDE5FF), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF7B61FF))
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8A84A0)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D1B69)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7A6F9B)
                )
            }
        }
    }
}

private fun getStudentLevel(points: Int): String {
    return when {
        points >= 1000 -> "Лидер"
        points >= 500 -> "Активист"
        else -> "Новичок"
    }
}

private fun getLevelDescription(points: Int): String {
    return when {
        points >= 1000 -> "Ты входишь в число самых активных студентов"
        points >= 500 -> "Ты стабильно выполняешь задания"
        else -> "Выполняй задания, чтобы повысить уровень"
    }
}
