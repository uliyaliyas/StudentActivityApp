package com.example.studentactivityapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val avatarColors = listOf(
    Color(0xFF7B61FF),
    Color(0xFF43A047),
    Color(0xFF1565C0),
    Color(0xFFE91E8C),
    Color(0xFFFF8A00),
    Color(0xFF00897B),
    Color(0xFF8D6E63),
    Color(0xFF546E7A)
)

private fun colorForName(name: String): Color {
    val index = if (name.isBlank()) 0
                else name.trim().hashCode().and(0x7FFFFFFF) % avatarColors.size
    return avatarColors[index]
}

@Composable
fun InitialsAvatar(
    name: String,
    size: Dp = 48.dp,
    fontSize: TextUnit = 20.sp
) {
    val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val color = colorForName(name)

    Box(
        modifier = Modifier
            .size(size)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize
        )
    }
}
