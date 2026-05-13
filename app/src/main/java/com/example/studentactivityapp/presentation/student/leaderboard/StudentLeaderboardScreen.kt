package com.example.studentactivityapp.presentation.student.leaderboard

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import com.example.studentactivityapp.ui.components.InitialsAvatar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import com.example.studentactivityapp.ui.components.appGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentLeaderboardScreen(
    innerPadding: PaddingValues,
    onBackClick: () -> Unit,
    viewModel: StudentLeaderboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val gradient = appGradient

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(innerPadding)
    ) {
        TopAppBar(
            title = {
                Text("Рейтинг студентов", fontWeight = FontWeight.Bold, color = Color(0xFF2D1B69))
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color(0xFF7B61FF))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

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
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.error!!, color = Color.Red)
                }
            }
            uiState.entries.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет данных", color = Color(0xFF7A6F9B))
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    item {
                        val top3 = uiState.entries.take(3)
                        if (top3.isNotEmpty()) {
                            PodiumSection(top3)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    uiState.currentUserRank?.let { rank ->
                        if (rank > 3) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFEDE5FF),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Твоя позиция: #$rank",
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF7B61FF)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    val rest = uiState.entries.drop(3)
                    if (rest.isNotEmpty()) {
                        item {
                            Text(
                                text = "Все участники",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D1B69),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(rest, key = { it.user.uid }) { entry ->
                            LeaderboardRow(entry)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun PodiumSection(top3: List<LeaderboardEntry>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFB300))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Топ-3",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D1B69)
                )
            }

            top3.forEach { entry ->
                PodiumItem(entry)
                if (entry.rank < top3.size) Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PodiumItem(entry: LeaderboardEntry) {
    val (medalColor, medalText) = when (entry.rank) {
        1 -> Color(0xFFFFB300) to "🥇"
        2 -> Color(0xFF90A4AE) to "🥈"
        else -> Color(0xFFBF8970) to "🥉"
    }
    val bgColor = if (entry.isCurrentUser) Color(0xFFEDE5FF) else Color(0xFFFAF8FF)

    Surface(shape = RoundedCornerShape(14.dp), color = bgColor) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = medalText, fontSize = 22.sp)
            Spacer(modifier = Modifier.width(12.dp))
            InitialsAvatar(name = entry.user.name, size = 36.dp, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.user.name.ifBlank { "Студент" },
                    fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.SemiBold,
                    color = Color(0xFF2D1B69),
                    style = MaterialTheme.typography.bodyMedium
                )
                if (entry.isCurrentUser) {
                    Text(text = "Это ты", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7B61FF))
                }
            }
            Text(
                text = "${entry.user.points} б.",
                fontWeight = FontWeight.Bold,
                color = medalColor,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    val bgColor = if (entry.isCurrentUser) Color(0xFFEDE5FF) else Color.White
    val borderColor = if (entry.isCurrentUser) Color(0xFF7B61FF) else Color.Transparent

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (entry.isCurrentUser) 3.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${entry.rank}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7A6F9B),
                modifier = Modifier.width(36.dp)
            )
            InitialsAvatar(name = entry.user.name, size = 34.dp, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.user.name.ifBlank { "Студент" },
                    fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Normal,
                    color = Color(0xFF2D1B69),
                    style = MaterialTheme.typography.bodyMedium
                )
                if (entry.isCurrentUser) {
                    Text(text = "Это ты", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7B61FF))
                }
            }
            Text(
                text = "${entry.user.points} б.",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7B61FF),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
