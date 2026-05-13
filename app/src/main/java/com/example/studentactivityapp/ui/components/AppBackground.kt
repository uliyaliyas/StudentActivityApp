package com.example.studentactivityapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val appGradient: Brush
    get() = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFFECDFFF),
            0.45f to Color(0xFFF5EEFF),
            1.0f to Color(0xFFFEFBFF)
        )
    )

@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    showBlobs: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(appGradient)
    ) {
        if (showBlobs) {
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 90.dp, y = (-80).dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0x70C9AEFF), Color.Transparent)
                        ),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-70).dp, y = 70.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0x60BBA4FF), Color.Transparent)
                        ),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.TopStart)
                    .offset(x = (-50).dp, y = 100.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0x40D4C0FF), Color.Transparent)
                        ),
                        CircleShape
                    )
            )
        }
        content()
    }
}
