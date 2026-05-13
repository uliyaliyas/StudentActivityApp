package com.example.studentactivityapp.presentation.student.tasks

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studentactivityapp.data.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.studentactivityapp.ui.components.appGradient

@Composable
fun StudentTasksScreen(
    innerPadding: PaddingValues,
    viewModel: StudentTasksViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }

    val gradient = appGradient

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(innerPadding)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Задания",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (uiState.searchQuery.isBlank()) "Доступно: ${uiState.totalCount}"
                   else "Найдено: ${uiState.tasks.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF7A6F9B)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.search(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Поиск по названию", color = Color(0xFFAAAAAA)) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF7B61FF))
            },
            trailingIcon = {
                if (uiState.searchQuery.isNotBlank()) {
                    IconButton(onClick = { viewModel.search("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Очистить", tint = Color(0xFF7A6F9B))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7B61FF),
                unfocusedBorderColor = Color(0xFFD8D0F0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        uiState.error?.let {
            Text(text = it, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF7B61FF))
                }
            }
            uiState.tasks.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (uiState.searchQuery.isBlank()) "Нет доступных заданий"
                               else "Ничего не найдено",
                        color = Color(0xFF7A6F9B)
                    )
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(uiState.tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            isPending = uiState.pendingTaskIds.contains(task.id),
                            onSubmit = { viewModel.submitTask(task) }
                        )
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    isPending: Boolean = false,
    onSubmit: () -> Unit
) {
    val completed = task.isCompleted
    val iconBg = when {
        completed -> Color(0xFFE3F2E8)
        isPending -> Color(0xFFFFF3E0)
        else -> Color(0xFFEDE5FF)
    }
    val iconTint = when {
        completed -> Color(0xFF43A047)
        isPending -> Color(0xFFF57C00)
        else -> Color(0xFF7B61FF)
    }
    val cardBg = when {
        completed -> Color(0xFFF1EDF6)
        isPending -> Color(0xFFFFFBF0)
        else -> Color.White
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = iconBg, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (completed) Icons.Default.CheckCircle else Icons.Default.TaskAlt,
                        contentDescription = null,
                        tint = iconTint
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1B69)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF7A6F9B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF1EBFF)) {
                    Text(
                        text = "+${task.points} баллов",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = Color(0xFF7B61FF),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = when {
                        completed -> Color(0xFFE6F4EA)
                        isPending -> Color(0xFFFFF3E0)
                        else -> Color(0xFFFFF4E5)
                    }
                ) {
                    Text(
                        text = when {
                            completed -> "Выполнено"
                            isPending -> "На проверке"
                            else -> "Активно"
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = when {
                            completed -> Color(0xFF43A047)
                            isPending -> Color(0xFFF57C00)
                            else -> Color(0xFFE38B00)
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (task.deadline > 0L) {
                    val now = System.currentTimeMillis()
                    val days = ((task.deadline - now) / 86_400_000).toInt()
                    val (chipColor, chipText, textColor) = when {
                        days < 0 -> Triple(Color(0xFFFFEBEE), "просрочено", Color(0xFFE53935))
                        days == 0 -> Triple(Color(0xFFFFEBEE), "сегодня", Color(0xFFE53935))
                        days <= 3 -> Triple(Color(0xFFFFF3E0), "осталось $days дн.", Color(0xFFF57C00))
                        else -> Triple(
                            Color(0xFFE8F5E9),
                            "до ${SimpleDateFormat("d MMM", Locale("ru")).format(Date(task.deadline))}",
                            Color(0xFF388E3C)
                        )
                    }
                    Surface(shape = RoundedCornerShape(14.dp), color = chipColor) {
                        Text(
                            text = chipText,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            color = textColor,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onSubmit,
                enabled = !completed && !isPending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7B61FF),
                    disabledContainerColor = when {
                        isPending -> Color(0xFFFFE0B2)
                        else -> Color(0xFFD5CEDF)
                    }
                )
            ) {
                Text(
                    text = when {
                        completed -> "Задание выполнено"
                        isPending -> "Ожидает подтверждения"
                        else -> "Отправить на проверку"
                    },
                    fontWeight = FontWeight.SemiBold,
                    color = if (isPending) Color(0xFFF57C00) else Color.White
                )
            }
        }
    }
}
