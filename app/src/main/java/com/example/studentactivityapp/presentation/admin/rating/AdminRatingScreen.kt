package com.example.studentactivityapp.presentation.admin.rating

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AdminRatingScreen(
    innerPadding: PaddingValues,
    viewModel: AdminRatingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRating()
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF7F3FF), Color(0xFFFFFFFF))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(innerPadding)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Рейтинг",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D1B69)
                )
                Text(
                    text = "Студенты по количеству баллов",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF7A6F9B)
                )
            }
            IconButton(onClick = { viewModel.loadRating() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Обновить",
                    tint = Color(0xFF7B61FF)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val currentStudents = uiState.students
        val currentLoading = uiState.isLoading
        val currentError = uiState.error

        when {
            currentLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF7B61FF))
                }
            }
            currentError != null -> {
                Text(text = currentError, color = Color.Red)
            }
            currentStudents.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет данных о студентах", color = Color(0xFF7A6F9B))
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    itemsIndexed(currentStudents) { index, student ->
                        RatingStudentCard(
                            place = index + 1,
                            name = student.name,
                            points = student.points
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingStudentCard(
    place: Int,
    name: String,
    points: Int
) {
    val medalColor = when (place) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> Color(0xFFEDE5FF)
    }
    val iconTextColor = when (place) {
        in 1..3 -> Color.Black
        else -> Color(0xFF7B61FF)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(medalColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (place <= 3) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = iconTextColor,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = place.toString(),
                        fontWeight = FontWeight.Bold,
                        color = iconTextColor,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = if (name.isNotEmpty()) name else "—",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1B69)
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1EBFF)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFF7B61FF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = points.toString(),
                        color = Color(0xFF2D1B69),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
