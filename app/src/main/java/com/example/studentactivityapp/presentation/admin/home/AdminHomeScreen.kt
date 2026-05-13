package com.example.studentactivityapp.presentation.admin.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studentactivityapp.presentation.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    innerPadding: PaddingValues,
    onLogoutClick: () -> Unit,
    onRewardsClick: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel(),
    viewModel: AdminHomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF7F3FF), Color.White)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Text(
            text = "Панель администратора",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Управление студентами, заданиями и активностью",
            color = Color(0xFF7A6F9B)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdminMiniStatCard(
                modifier = Modifier.weight(1f),
                title = "Студенты",
                value = uiState.studentsCount.toString(),
                icon = Icons.Default.Groups,
                color = Color(0xFF7B61FF)
            )
            AdminMiniStatCard(
                modifier = Modifier.weight(1f),
                title = "Задания",
                value = uiState.tasksCount.toString(),
                icon = Icons.Default.Assignment,
                color = Color(0xFF43A047)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdminMiniStatCard(
                modifier = Modifier.weight(1f),
                title = "Средний балл",
                value = uiState.averagePoints.toString(),
                icon = Icons.Default.Star,
                color = Color(0xFFFF8A00)
            )
            AdminMiniStatCard(
                modifier = Modifier.weight(1f),
                title = "Лидер",
                value = uiState.topStudent.take(12),
                icon = Icons.Default.EmojiEvents,
                color = Color(0xFFE53935)
            )
        }

        uiState.error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.openNotificationSheet() },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C4EE5))
        ) {
            Icon(Icons.Default.Notifications, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Написать студентам", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onRewardsClick,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B61FF))
        ) {
            Icon(Icons.Default.CardGiftcard, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Управление наградами", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                authViewModel.logout()
                onLogoutClick()
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Выйти из аккаунта", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    if (uiState.showNotificationSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeNotificationSheet() },
            sheetState = sheetState
        ) {
            NotificationSheetContent(
                form = uiState.notificationForm,
                sent = uiState.notificationSent,
                onTitleChange = { viewModel.updateNotificationTitle(it) },
                onBodyChange = { viewModel.updateNotificationBody(it) },
                onSend = { viewModel.sendNotification() },
                onClose = { viewModel.closeNotificationSheet() }
            )
        }
    }
}

@Composable
private fun NotificationSheetContent(
    form: NotificationFormState,
    sent: Boolean,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onSend: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp)
            .imePadding()
    ) {
        Text(
            text = "Сообщение студентам",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Уведомление получат все студенты",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF7A6F9B)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (sent) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF43A047))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Уведомление отправлено!",
                    color = Color(0xFF43A047),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text("Закрыть", color = Color(0xFF7A6F9B))
            }
        } else {
            OutlinedTextField(
                value = form.title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Заголовок") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7B61FF),
                    unfocusedBorderColor = Color(0xFFD8D0F0)
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = form.body,
                onValueChange = onBodyChange,
                modifier = Modifier.fillMaxWidth().height(110.dp),
                label = { Text("Текст сообщения") },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7B61FF),
                    unfocusedBorderColor = Color(0xFFD8D0F0)
                )
            )
            form.error?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onSend,
                enabled = form.isValid && !form.isSending,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5C4EE5),
                    disabledContainerColor = Color(0xFFD0C8F0)
                )
            ) {
                if (form.isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Notifications, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Отправить всем", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text("Отмена", color = Color(0xFF7A6F9B))
            }
        }
    }
}

@Composable
private fun AdminMiniStatCard(
    modifier: Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1B69)
            )
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = Color(0xFF8A84A0))
        }
    }
}
