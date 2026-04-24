package com.example.studentactivityapp.presentation.student.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAchievementsScreen(
    innerPadding: PaddingValues,
    onBackClick: () -> Unit,
    viewModel: StudentAchievementsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAchievements()
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF7F3FF),
            Color.White
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(innerPadding)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Достижения",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D1B69)
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color(0xFF2D1B69)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Открывай достижения за активность",
                color = Color(0xFF7A6F9B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            uiState.error?.let {
                Text(text = it, color = Color.Red)
                Spacer(modifier = Modifier.height(12.dp))
            }

            LazyColumn {
                items(uiState.achievements) { achievement ->
                    AchievementCard(achievement)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: AchievementItem
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) Color.White else Color(0xFFF2F2F2)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Icon(
                imageVector = if (achievement.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                contentDescription = null,
                tint = if (achievement.isUnlocked) Color(0xFF7B61FF) else Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = achievement.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1B69)
            )

            Text(
                text = achievement.description,
                color = Color(0xFF7A6F9B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = achievement.progressText,
                fontWeight = FontWeight.SemiBold,
                color = if (achievement.isUnlocked) Color(0xFF7B61FF) else Color.Gray
            )
        }
    }
}