package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

data class ConfettiParticle(
    val xRatio: Float,
    val speed: Float,
    val color: Color,
    val width: Float,
    val height: Float,
    val rotationSpeed: Float,
    val wobbleSpeed: Float
)

@Composable
fun ConfettiOverlay(
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    val particles = remember {
        val colors = listOf(
            Color(0xFFFF1744), Color(0xFFFF6D00), Color(0xFFFFD600),
            Color(0xFF00E676), Color(0xFF00B0FF), Color(0xFF651FFF),
            Color(0xFFFF4081), Color(0xFF00E5FF)
        )
        List(85) {
            ConfettiParticle(
                xRatio = Random.nextFloat(),
                speed = Random.nextFloat() * 0.6f + 0.7f,
                color = colors.random(),
                width = Random.nextFloat() * 16f + 14f,
                height = Random.nextFloat() * 12f + 8f,
                rotationSpeed = Random.nextFloat() * 720f - 360f,
                wobbleSpeed = Random.nextFloat() * 8f + 3f
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3200, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val p = progress.value

        particles.forEach { particle ->
            val y = -50f + (h + 120f) * (p * particle.speed)
            if (y in -50f..(h + 50f)) {
                val wobble = kotlin.math.sin(p * particle.wobbleSpeed * Math.PI.toFloat()) * 30f
                val x = (particle.xRatio * w) + wobble
                val angle = p * particle.rotationSpeed

                rotate(degrees = angle, pivot = Offset(x, y)) {
                    drawRect(
                        color = particle.color,
                        topLeft = Offset(x - particle.width / 2, y - particle.height / 2),
                        size = Size(particle.width, particle.height)
                    )
                }
            }
        }
    }
}
