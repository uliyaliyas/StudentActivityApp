package com.example.studentactivityapp.presentation.admin.statistics

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentactivityapp.data.model.User

@Composable
fun AdminStatisticsScreen(
    innerPadding: PaddingValues,
    viewModel: AdminStatisticsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadStatistics()
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF7F3FF), Color(0xFFFFFFFF))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Статистика",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D1B69)
                )
                Text(
                    text = "Анализ активности студентов",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF7A6F9B)
                )
            }
            IconButton(onClick = { viewModel.loadStatistics() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Обновить",
                    tint = Color(0xFF7B61FF)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val currentLoading = uiState.isLoading

        if (currentLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF7B61FF))
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Студенты",
                    value = uiState.studentsCount.toString(),
                    icon = Icons.Default.Groups,
                    color = Color(0xFF7B61FF)
                )
                StatMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Задания",
                    value = uiState.tasksCount.toString(),
                    icon = Icons.Default.Assignment,
                    color = Color(0xFF9C7BFF)
                )
                StatMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Средний балл",
                    value = uiState.averagePoints.toString(),
                    icon = Icons.Default.Star,
                    color = Color(0xFFB39DFF)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val topStudents = uiState.topStudents
            if (topStudents.isNotEmpty()) {
                Text(
                    text = "Топ студентов",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D1B69)
                )

                Spacer(modifier = Modifier.height(12.dp))

                val maxPoints = topStudents.maxOf { it.points }.coerceAtLeast(1)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        topStudents.forEachIndexed { index, student ->
                            StudentBarRow(
                                student = student,
                                maxPoints = maxPoints,
                                place = index + 1
                            )
                            if (index < topStudents.lastIndex) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun StatMiniCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFEDE5FF), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8A84A0),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun StudentBarRow(
    student: User,
    maxPoints: Int,
    place: Int
) {
    val barColor = Brush.horizontalGradient(
        colors = listOf(Color(0xFF7B61FF), Color(0xFFB39DFF))
    )
    val fraction = (student.points.toFloat() / maxPoints).coerceIn(0.05f, 1f)

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$place.",
            color = Color(0xFF7B61FF),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.width(24.dp)
        )

        Text(
            text = if (student.name.isNotEmpty()) student.name else "—",
            modifier = Modifier.width(100.dp),
            color = Color(0xFF2D1B69),
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(20.dp)
                .background(Color(0xFFF4EEFF), RoundedCornerShape(10.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(20.dp)
                    .background(barColor, RoundedCornerShape(10.dp))
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = student.points.toString(),
            color = Color(0xFF7B61FF),
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}
