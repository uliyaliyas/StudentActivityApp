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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StudentSnakeScreen(
    innerPadding: PaddingValues,
    viewModel: SnakeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1035), Color(0xFF0D0820))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(innerPadding)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Змейка",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Очки: ${uiState.score}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF9C7BFF)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Баллов сегодня",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7A6F9B)
                )
                Text(
                    text = "${uiState.dailyPointsEarned} / ${uiState.dailyLimit}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7B61FF)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFF120D2A), RoundedCornerShape(16.dp))
        ) {
            val snake = uiState.snake
            val food = uiState.food
            val gameState = uiState.gameState

            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellW = size.width / SnakeViewModel.GRID_SIZE
                val cellH = size.height / SnakeViewModel.GRID_SIZE

                snake.forEachIndexed { index, (cx, cy) ->
                    val color = if (index == 0) Color(0xFF7B61FF) else Color(0xFF9C7BFF)
                    drawRect(
                        color = color,
                        topLeft = Offset(cx * cellW + 1f, cy * cellH + 1f),
                        size = Size(cellW - 2f, cellH - 2f)
                    )
                }

                drawCircle(
                    color = Color(0xFFFF5252),
                    radius = (cellW.coerceAtMost(cellH)) / 2f - 2f,
                    center = Offset(
                        food.first * cellW + cellW / 2f,
                        food.second * cellH + cellH / 2f
                    )
                )
            }

            if (gameState == GameState.IDLE) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x99000000), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нажми «Старт»",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (gameState == GameState.GAME_OVER) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x99000000), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Игра окончена",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Очки: ${uiState.score}",
                            color = Color(0xFF9C7BFF),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val currentMessage = uiState.message
        if (currentMessage != null) {
            Text(
                text = currentMessage,
                color = Color(0xFF9C7BFF),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(6.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DPadButton(icon = Icons.Default.KeyboardArrowUp) {
                viewModel.changeDirection(Direction.UP)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DPadButton(icon = Icons.Default.KeyboardArrowLeft) {
                    viewModel.changeDirection(Direction.LEFT)
                }
                Box(modifier = Modifier.size(48.dp))
                DPadButton(icon = Icons.Default.KeyboardArrowRight) {
                    viewModel.changeDirection(Direction.RIGHT)
                }
            }
            DPadButton(icon = Icons.Default.KeyboardArrowDown) {
                viewModel.changeDirection(Direction.DOWN)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val gameState = uiState.gameState
        Button(
            onClick = { viewModel.startGame() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B61FF))
        ) {
            Text(
                text = if (gameState == GameState.IDLE) "Старт" else "Заново",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun DPadButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color(0xFF2D1B69)
        )
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White)
    }
}
