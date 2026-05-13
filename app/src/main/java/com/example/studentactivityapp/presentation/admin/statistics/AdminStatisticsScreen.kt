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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
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

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF7B61FF))
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                    title = "Активные",
                    value = uiState.activeStudentsCount.toString(),
                    icon = Icons.Default.Person,
                    color = Color(0xFF43A047)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Задания",
                    value = uiState.tasksCount.toString(),
                    icon = Icons.Default.Assignment,
                    color = Color(0xFF9C7BFF)
                )
                StatMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Награды",
                    value = uiState.rewardsCount.toString(),
                    icon = Icons.Default.CardGiftcard,
                    color = Color(0xFFE91E8C)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "Баллов выдано",
                    value = uiState.totalPointsDistributed.toString(),
                    icon = Icons.Default.Star,
                    color = Color(0xFFFFB300)
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

            if (uiState.pointsBuckets.isNotEmpty() && uiState.studentsCount > 0) {
                PointsDistributionChart(uiState.pointsBuckets)
                Spacer(modifier = Modifier.height(20.dp))
            }

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
private fun PointsDistributionChart(buckets: List<PointsBucket>) {
    val barColor = Color(0xFF7B61FF)
    val emptyColor = Color(0xFFEDE5FF)
    val labelColor = Color(0xFF8A84A0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Распределение студентов по баллам",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF7A6F9B)
            )
            Spacer(modifier = Modifier.height(12.dp))

            val maxCount = buckets.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1

            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                val n = buckets.size
                val gap = 10.dp.toPx()
                val barW = (size.width - gap * (n + 1)) / n
                val maxBarH = size.height - 24.dp.toPx()

                buckets.forEachIndexed { i, bucket ->
                    val x = gap + i * (barW + gap)
                    val barH = if (bucket.count > 0) (bucket.count.toFloat() / maxCount) * maxBarH else 4.dp.toPx()
                    val y = size.height - barH - 18.dp.toPx()
                    val color = if (bucket.count == 0) emptyColor else barColor

                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, y),
                        size = Size(barW, barH),
                        cornerRadius = CornerRadius(6.dp.toPx())
                    )

                    if (bucket.count > 0) {
                        drawContext.canvas.nativeCanvas.drawText(
                            "${bucket.count}",
                            x + barW / 2,
                            y - 4.dp.toPx(),
                            android.graphics.Paint().apply {
                                textAlign = android.graphics.Paint.Align.CENTER
                                textSize = 11.sp.toPx()
                                this.color = barColor.toArgb()
                                isFakeBoldText = true
                            }
                        )
                    }

                    drawContext.canvas.nativeCanvas.drawText(
                        bucket.label,
                        x + barW / 2,
                        size.height,
                        android.graphics.Paint().apply {
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 9.sp.toPx()
                            this.color = labelColor.toArgb()
                        }
                    )
                }
            }
        }
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
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
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
    val fraction = (student.points.toFloat() / maxPoints).coerceIn(0.05f, 1f)
    val barColor = Brush.horizontalGradient(
        colors = listOf(Color(0xFF7B61FF), Color(0xFFB39DFF))
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
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
