package com.troves.presintation.ui.orderresult.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.onSizeChanged
import com.troves.designsystem.theme.Theme
import kotlin.random.Random

@Composable
fun ConfettiCelebration(
    modifier: Modifier = Modifier,
    running: Boolean = true,
    durationMillis: Int = 4000,
    particleCount: Int = 140,
) {
    if (!running) return

    val colors = listOf(
        Theme.colors.primary,
        Theme.colors.success,
        Theme.colors.amber,
        Theme.colors.warning,
        Theme.colors.tint,
        Color(0xFFFFD700), // gold
        Color(0xFFFF6B9D), // pink
    )

    val rnd = remember { Random(0x5EED) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val particles = remember { mutableStateListOf<Particle>() }
    var frameTick by remember { mutableIntStateOf(0) }

    LaunchedEffect(canvasSize) {
        if (canvasSize == Size.Zero || particles.isNotEmpty()) return@LaunchedEffect
        emitParticles(particles, canvasSize, particleCount, colors, rnd)
    }

    LaunchedEffect(canvasSize) {
        if (canvasSize == Size.Zero) return@LaunchedEffect
        val durationSeconds = durationMillis / 1000f
        val fadeStart = durationSeconds - 0.8f
        var last = 0L
        var elapsed = 0f
        while (elapsed < durationSeconds) {
            withFrameNanos { now ->
                val dt = if (last == 0L) 0f else (now - last) / 1_000_000_000f
                last = now
                elapsed += dt
                particles.forEach { p ->
                    p.vy += GRAVITY * dt
                    p.vx += (rnd.nextFloat() - 0.5f) * WOBBLE * dt
                    p.x += p.vx * dt
                    p.y += p.vy * dt
                    p.rotation += p.rotationSpeed * dt
                    if (elapsed > fadeStart) {
                        p.alpha = (p.alpha - dt / 0.8f).coerceAtLeast(0f)
                    }
                }
                frameTick++
            }
        }
        particles.clear()
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
    ) {
        @Suppress("UNUSED_EXPRESSION")
        frameTick // read so Compose redraws each frame
        particles.forEach { p ->
            val color = p.color.copy(alpha = p.alpha)
            if (p.isCircle) {
                drawCircle(color = color, radius = p.size / 2f, center = Offset(p.x, p.y))
            } else {
                rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
                    drawRect(
                        color = color,
                        topLeft = Offset(p.x - p.size / 2f, p.y - p.size / 3f),
                        size = Size(p.size, p.size * 0.6f),
                    )
                }
            }
        }
    }
}

private const val GRAVITY = 500f
private const val WOBBLE = 60f

private fun emitParticles(
    particles: SnapshotStateList<Particle>,
    canvasSize: Size,
    count: Int,
    colors: List<Color>,
    rnd: Random,
) {
    repeat(count) {
        particles.add(
            Particle(
                x = rnd.nextFloat() * canvasSize.width,
                y = -rnd.nextFloat() * canvasSize.height * 0.5f, // start above the top edge
                vx = (rnd.nextFloat() - 0.5f) * 240f,
                vy = 300f + rnd.nextFloat() * 320f,
                rotation = rnd.nextFloat() * 360f,
                rotationSpeed = (rnd.nextFloat() - 0.5f) * 600f,
                color = colors[rnd.nextInt(colors.size)],
                size = 14f + rnd.nextFloat() * 14f,
                isCircle = rnd.nextFloat() < 0.25f,
            )
        )
    }
}

private class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var rotation: Float,
    val rotationSpeed: Float,
    val color: Color,
    val size: Float,
    val isCircle: Boolean,
    var alpha: Float = 1f,
)
