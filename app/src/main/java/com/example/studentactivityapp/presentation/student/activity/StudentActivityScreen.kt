package com.example.studentactivityapp.presentation.student.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentactivityapp.data.model.CompletedTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentActivityScreen(
    innerPadding: PaddingValues,
    onBackClick: () -> Unit,
    viewModel: StudentActivityViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCompletedTasks()
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF7F3FF), Color.White)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(innerPadding)
    ) {
        TopAppBar(
            title = {
                Text("Моя активность", fontWeight = FontWeight.Bold, color = Color(0xFF2D1B69))
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color(0xFF7B61FF))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterPeriod.entries.forEach { period ->
                val selected = uiState.selectedFilter == period
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.setFilter(period) },
                    label = { Text(period.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF7B61FF),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val isLoading = uiState.isLoading
        val tasks = uiState.completedTasks
        val error = uiState.error

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF7B61FF))
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error, color = Color.Red)
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    item {
                        val chartBars = uiState.chartBars
                        if (chartBars.isNotEmpty()) {
                            ActivityBarChart(
                                bars = chartBars,
                                period = uiState.selectedFilter
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    item {
                        PeriodSummaryCard(
                            count = tasks.size,
                            points = uiState.totalPoints,
                            period = uiState.selectedFilter
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    if (tasks.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Нет заданий за выбранный период", color = Color(0xFF7A6F9B))
                            }
                        }
                    } else {
                        items(tasks) { task ->
                            CompletedTaskItem(task)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityBarChart(bars: List<ChartBar>, period: FilterPeriod) {
    val barColor = Color(0xFF7B61FF)
    val barColorToday = Color(0xFF5C4EE5)
    val emptyColor = Color(0xFFEDE5FF)
    val labelColor = Color(0xFF8A84A0)
    val valueColor = Color(0xFF7B61FF)
    val title = when (period) {
        FilterPeriod.MONTH -> "Баллы по неделям (месяц)"
        else -> "Баллы по дням (7 дней)"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF7A6F9B),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            val maxPoints = bars.maxOfOrNull { it.points }?.coerceAtLeast(1) ?: 1

            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                val n = bars.size
                val gap = 8.dp.toPx()
                val barW = (size.width - gap * (n + 1)) / n
                val maxBarH = size.height - 22.dp.toPx()

                bars.forEachIndexed { i, bar ->
                    val x = gap + i * (barW + gap)
                    val barH = (bar.points.toFloat() / maxPoints) * maxBarH
                    val y = size.height - barH - 18.dp.toPx()

                    val color = when {
                        bar.points == 0 -> emptyColor
                        bar.isToday -> barColorToday
                        else -> barColor
                    }

                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, if (bar.points == 0) size.height - 4.dp.toPx() - 18.dp.toPx() else y),
                        size = Size(barW, if (bar.points == 0) 4.dp.toPx() else barH),
                        cornerRadius = CornerRadius(6.dp.toPx())
                    )

                    if (bar.points > 0) {
                        drawContext.canvas.nativeCanvas.drawText(
                            "${bar.points}",
                            x + barW / 2,
                            (if (bar.points == 0) size.height - 4.dp.toPx() - 18.dp.toPx() else y) - 4.dp.toPx(),
                            android.graphics.Paint().apply {
                                textAlign = android.graphics.Paint.Align.CENTER
                                textSize = 10.sp.toPx()
                                this.color = valueColor.toArgb()
                                isFakeBoldText = true
                            }
                        )
                    }

                    drawContext.canvas.nativeCanvas.drawText(
                        bar.label,
                        x + barW / 2,
                        size.height,
                        android.graphics.Paint().apply {
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 10.sp.toPx()
                            this.color = if (bar.isToday) barColorToday.toArgb() else labelColor.toArgb()
                            if (bar.isToday) isFakeBoldText = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PeriodSummaryCard(count: Int, points: Int, period: FilterPeriod) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4EEFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFEDE5FF), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFF7B61FF))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = period.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7A6F9B)
                )
                Text(
                    text = "$count заданий · +$points баллов",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D1B69)
                )
            }
        }
    }
}

@Composable
private fun CompletedTaskItem(task: CompletedTask) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF43A047), modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D1B69)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatDate(task.completedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8A84A0)
                )
            }
            Text(
                text = "+${task.points}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7B61FF)
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "Дата неизвестна"
    return SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("ru")).format(Date(timestamp))
}
