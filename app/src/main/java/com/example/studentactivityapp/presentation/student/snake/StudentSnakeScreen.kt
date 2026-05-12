package com.example.studentactivityapp.presentation.student.snake

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StudentSnakeScreen(
    innerPadding: PaddingValues,
    viewModel: SnakeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF7F3FF), Color.White)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(innerPadding)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Игра Змейка",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69)
        )

        Spacer(modifier = Modifier.height(2.dp))

        if (!uiState.isGuest) {
            Text(
                text = "Баллы сегодня: ${uiState.dailyPointsEarned} / ${uiState.dailyLimit}",
                color = Color(0xFF7A6F9B),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEDE5FF)
            ) {
                Text(
                    text = "Счёт: ${uiState.score}",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D1B69),
                    fontSize = 16.sp
                )
            }

            if (uiState.gameState == GameState.GAME_OVER && uiState.message != null) {
                Text(
                    text = uiState.message,
                    color = if (uiState.message.startsWith("+")) Color(0xFF4CAF50)
                            else Color(0xFF7A6F9B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1035)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(4.dp)
            ) {
                val cellSize = size.width / SnakeViewModel.GRID_SIZE

                drawCircle(
                    color = Color(0xFFFF5252),
                    radius = cellSize / 2.4f,
                    center = Offset(
                        x = uiState.food.first * cellSize + cellSize / 2f,
                        y = uiState.food.second * cellSize + cellSize / 2f
                    )
                )

                uiState.snake.forEachIndexed { index, segment ->
                    val color = if (index == 0) Color(0xFF7B61FF) else Color(0xFF9C7BFF)
                    val inset = if (index == 0) 1.5f else 2.5f
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(
                            x = segment.first * cellSize + inset,
                            y = segment.second * cellSize + inset
                        ),
                        size = Size(cellSize - inset * 2f, cellSize - inset * 2f),
                        cornerRadius = CornerRadius(cellSize / 4f)
                    )
                }

                if (uiState.gameState == GameState.GAME_OVER) {
                    drawRect(color = Color(0x99000000), size = size)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (uiState.gameState == GameState.GAME_OVER) {
            Text(
                text = "Игра окончена — счёт: ${uiState.score}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1B69),
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (uiState.gameState != GameState.PLAYING) {
            Button(
                onClick = { viewModel.startGame() },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B61FF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (uiState.gameState == GameState.IDLE) "Начать игру"
                           else "Играть снова",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        if (uiState.gameState == GameState.PLAYING) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DpadButton(
                    icon = Icons.Default.ArrowUpward,
                    onClick = { viewModel.changeDirection(Direction.UP) }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DpadButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = { viewModel.changeDirection(Direction.LEFT) }
                    )
                    Spacer(modifier = Modifier.width(54.dp))
                    DpadButton(
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        onClick = { viewModel.changeDirection(Direction.RIGHT) }
                    )
                }
                DpadButton(
                    icon = Icons.Default.ArrowDownward,
                    onClick = { viewModel.changeDirection(Direction.DOWN) }
                )
            }
        }
    }
}

@Composable
private fun DpadButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(54.dp),
        shape = RoundedCornerShape(14.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color(0xFFEDE5FF),
            contentColor = Color(0xFF7B61FF)
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(26.dp)
        )
    }
}
