package com.jvrni.core.ui.components

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jvrni.core.ui.theme.NewsAppTheme

private val DotSize = 10.dp
private val DotSpacing = 8.dp
private const val DotDurationMs = 500
private const val DotDelayMs = 150
private const val DotJump = 18f

private val BounceEasing = Easing { t ->
    -(kotlin.math.cos(Math.PI * t).toFloat() - 1f) / 2f
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "dots")

    val offsets = (0..2).map { index ->
        val offsetY by transition.animateFloat(
            initialValue = 0f,
            targetValue = -DotJump,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = DotDurationMs,
                    delayMillis = index * DotDelayMs,
                    easing = BounceEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot_$index"
        )
        offsetY
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(DotSpacing)) {
            offsets.forEach { offsetY ->
                Box(
                    modifier = Modifier
                        .size(DotSize)
                        .graphicsLayer { translationY = offsetY }
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrevLoadingScreen() {
    NewsAppTheme {
        LoadingScreen()
    }
}
