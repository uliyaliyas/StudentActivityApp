package com.example.studentactivityapp.presentation.student.rewards

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentactivityapp.data.model.Reward

@Composable
fun StudentRewardsScreen(
    innerPadding: PaddingValues,
    onHistoryClick: () -> Unit = {},
    viewModel: StudentRewardsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var rewardToConfirm by remember { mutableStateOf<Reward?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val currentSuccess = uiState.successMessage
    LaunchedEffect(currentSuccess) {
        if (currentSuccess != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessage()
        }
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF7F3FF), Color(0xFFFFFFFF))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(innerPadding)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Награды",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1B69),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onHistoryClick) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "История",
                    tint = Color(0xFF7B61FF)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Обменивай накопленные баллы на приятные бонусы",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF7A6F9B)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF4EEFF))
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFF7B61FF)
                )
                Spacer(modifier = Modifier.size(10.dp))
                Column {
                    Text(
                        text = "Твой баланс",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF7A6F9B)
                    )
                    val currentPoints = uiState.userPoints
                    Text(
                        text = "$currentPoints баллов",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1B69)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val currentError = uiState.error
        val currentSuccessMsg = uiState.successMessage

        if (currentError != null) {
            Text(text = currentError, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (currentSuccessMsg != null) {
            Text(text = currentSuccessMsg, color = Color(0xFF4CAF50), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        val currentLoading = uiState.isLoading
        val currentRewards = uiState.rewards

        when {
            currentLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF7B61FF))
                }
            }
            currentRewards.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Наград пока нет", color = Color(0xFF7A6F9B))
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(currentRewards) { reward ->
                        RewardCard(
                            reward = reward,
                            userPoints = uiState.userPoints,
                            onRedeemClick = { rewardToConfirm = reward }
                        )
                    }
                }
            }
        }
    }

    val pendingReward = rewardToConfirm
    if (pendingReward != null) {
        AlertDialog(
            onDismissRequest = { rewardToConfirm = null },
            title = {
                Text(
                    text = "Подтверждение",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D1B69)
                )
            },
            text = {
                Text(
                    text = "Получить «${pendingReward.title}» за ${pendingReward.points} баллов?",
                    color = Color(0xFF2D1B69)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.redeemReward(pendingReward)
                        rewardToConfirm = null
                    }
                ) {
                    Text("Получить", color = Color(0xFF7B61FF))
                }
            },
            dismissButton = {
                TextButton(onClick = { rewardToConfirm = null }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun RewardCard(
    reward: Reward,
    userPoints: Int,
    onRedeemClick: () -> Unit
) {
    val icon = rewardIcon(reward.iconName)
    val canAfford = userPoints >= reward.points

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color(0xFFEDE5FF), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF7B61FF)
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reward.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1B69)
                    )
                    Text(
                        text = reward.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8A84A0)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (canAfford) Color(0xFFF1EBFF) else Color(0xFFF5F5F5)
                ) {
                    Text(
                        text = "${reward.points} б.",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = if (canAfford) Color(0xFF7B61FF) else Color(0xFFAAAAAA),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onRedeemClick,
                enabled = canAfford,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7B61FF),
                    disabledContainerColor = Color(0xFFD0C8F0)
                )
            ) {
                Text(if (canAfford) "Получить награду" else "Недостаточно баллов")
            }
        }
    }
}

private fun rewardIcon(iconName: String): ImageVector {
    return when (iconName) {
        "cafe" -> Icons.Default.LocalCafe
        "food" -> Icons.Default.LunchDining
        "offer" -> Icons.Default.LocalOffer
        else -> Icons.Default.CardGiftcard
    }
}
