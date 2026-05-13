package com.example.studentactivityapp.presentation.student

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StudentHomeScreen(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    onRewardsClick: () -> Unit = {},
    onSnakeClick: () -> Unit = {},
    viewModel: StudentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    val userName = uiState.user?.name ?: "Студент"
    val points = uiState.user?.points ?: 0
    val level = getStudentLevel(points)
    val nextLevel = getNextLevelPoints(points)
    val progress = getProgress(points)

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF5F1FF),
            Color.White
        )
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
            text = "Главная",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Привет, $userName",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF7A6F9B)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFFEDE5FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF7B61FF)
                        )
                    }

                    Spacer(modifier = Modifier.size(12.dp))

                    Column {
                        Text(
                            text = "$points баллов",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D1B69)
                        )
                        Text(
                            text = "Звание: $level",
                            color = Color(0xFF7A6F9B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Прогресс уровня",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D1B69)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = Color(0xFF7B61FF),
                    trackColor = Color(0xFFE7DDFF)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$points / $nextLevel баллов",
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SmallStatCard(
                modifier = Modifier.weight(1f),
                title = "Серия",
                value = "3 дня",
                iconColor = Color(0xFFFF8A00),
                icon = Icons.Default.LocalFireDepartment
            )

            SmallStatCard(
                modifier = Modifier.weight(1f),
                title = "Задания",
                value = "Выполняй",
                iconColor = Color(0xFF43A047),
                icon = Icons.Default.TaskAlt
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                onClick = onRewardsClick,
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFFEDE5FF),
                shadowElevation = 4.dp,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = Color(0xFF7B61FF),
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Награды",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1B69)
                    )
                    Text(
                        text = "Магазин бонусов",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF7A6F9B)
                    )
                }
            }

            Surface(
                onClick = onSnakeClick,
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFFE8F5E9),
                shadowElevation = 4.dp,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        tint = Color(0xFF43A047),
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Змейка",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1B69)
                    )
                    Text(
                        text = "Зарабатывай баллы",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF7A6F9B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF4EEFF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFF7B61FF)
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = "Челлендж дня",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1B69)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Выполни одно задание сегодня и увеличь свою серию активности.",
                    color = Color(0xFF7A6F9B)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF43A047)
                )

                Spacer(modifier = Modifier.size(10.dp))

                Column {
                    Text(
                        text = "Совет",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1B69)
                    )
                    Text(
                        text = "Проверяй задания каждый день — так проще попасть в рейтинг.",
                        color = Color(0xFF7A6F9B)
                    )
                }
            }
        }

        uiState.error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun SmallStatCard(
    modifier: Modifier,
    title: String,
    value: String,
    iconColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = Color(0xFF7A6F9B)
            )

            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1B69)
            )
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

private fun getNextLevelPoints(points: Int): Int {
    return when {
        points < 100 -> 100
        points < 500 -> 500
        points < 1000 -> 1000
        else -> points
    }
}

private fun getProgress(points: Int): Float {
    val next = getNextLevelPoints(points)
    val prev = when {
        points < 100 -> 0
        points < 500 -> 100
        points < 1000 -> 500
        else -> next
    }

    return if (next == prev) 1f else (points - prev).toFloat() / (next - prev)
}