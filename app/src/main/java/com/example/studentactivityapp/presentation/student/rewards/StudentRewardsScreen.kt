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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StudentRewardsScreen(
    innerPadding: PaddingValues
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF7F3FF),
            Color(0xFFFFFFFF)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Награды",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Обменивай накопленные баллы на приятные бонусы",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF7A6F9B)
        )

        Spacer(modifier = Modifier.height(18.dp))

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
                    Text(
                        text = "1250 баллов",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1B69)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        RewardCard(
            title = "Чай",
            description = "Горячий напиток в буфете кафедры",
            points = "200",
            icon = Icons.Default.LocalCafe
        )

        Spacer(modifier = Modifier.height(12.dp))

        RewardCard(
            title = "Сосиска в тесте",
            description = "Небольшой перекус за баллы",
            points = "350",
            icon = Icons.Default.LunchDining
        )

        Spacer(modifier = Modifier.height(12.dp))

        RewardCard(
            title = "Фирменный стикер",
            description = "Наклейка с символикой кафедры",
            points = "150",
            icon = Icons.Default.LocalOffer
        )

        Spacer(modifier = Modifier.height(12.dp))

        RewardCard(
            title = "Подарочный набор",
            description = "Небольшой бонусный комплект",
            points = "800",
            icon = Icons.Default.CardGiftcard
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun RewardCard(
    title: String,
    description: String,
    points: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
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
                        .background(
                            color = Color(0xFFEDE5FF),
                            shape = RoundedCornerShape(14.dp)
                        ),
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
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1B69)
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8A84A0)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1EBFF)
                ) {
                    Text(
                        text = "$points б.",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = Color(0xFF7B61FF),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7B61FF)
                )
            ) {
                Text("Получить награду")
            }
        }
    }
}