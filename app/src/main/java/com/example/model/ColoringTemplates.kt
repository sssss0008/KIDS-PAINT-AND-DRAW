package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

object ColoringTemplates {

    val allTemplates = listOf(
        ColoringPage(
            id = "kitty",
            title = "Smiling Kitty",
            emoji = "🐱",
            description = "A friendly cute cat with a sweet bow tie!",
            category = "Animals"
        ),
        ColoringPage(
            id = "dino",
            title = "Happy Dino",
            emoji = "🦕",
            description = "A friendly little dinosaur under the sun!",
            category = "Animals"
        ),
        ColoringPage(
            id = "rocket",
            title = "Space Explorer",
            emoji = "🚀",
            description = "Fly high among the moon and sparkly stars!",
            category = "Space"
        ),
        ColoringPage(
            id = "ocean",
            title = "Undersea Turtle",
            emoji = "🐢",
            description = "A happy sea turtle swimming with bubbles!",
            category = "Nature"
        ),
        ColoringPage(
            id = "castle",
            title = "Rainbow Castle",
            emoji = "🏰",
            description = "Magical castle with a giant rainbow!",
            category = "Fantasy"
        )
    )

    fun drawTemplate(scope: DrawScope, templateId: String?) {
        if (templateId == null) return

        val width = scope.size.width
        val height = scope.size.height
        if (width <= 0f || height <= 0f) return

        val outlineColor = Color(0xFF2D3748)
        val stroke = Stroke(
            width = 6f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
        val thinStroke = Stroke(
            width = 3.5f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        when (templateId) {
            "kitty" -> drawKitty(scope, width, height, outlineColor, stroke, thinStroke)
            "dino" -> drawDino(scope, width, height, outlineColor, stroke, thinStroke)
            "rocket" -> drawRocket(scope, width, height, outlineColor, stroke, thinStroke)
            "ocean" -> drawOcean(scope, width, height, outlineColor, stroke, thinStroke)
            "castle" -> drawCastle(scope, width, height, outlineColor, stroke, thinStroke)
        }
    }

    private fun drawKitty(
        scope: DrawScope,
        w: Float,
        h: Float,
        color: Color,
        stroke: Stroke,
        thin: Stroke
    ) {
        val cx = w * 0.5f
        val cy = h * 0.42f
        val faceR = minOf(w, h) * 0.30f

        // Head
        scope.drawCircle(color, faceR, Offset(cx, cy), style = stroke)

        // Left Ear
        val leftEar = Path().apply {
            moveTo(cx - faceR * 0.85f, cy - faceR * 0.45f)
            lineTo(cx - faceR * 0.75f, cy - faceR * 1.25f)
            lineTo(cx - faceR * 0.20f, cy - faceR * 0.95f)
        }
        scope.drawPath(leftEar, color, style = stroke)

        // Right Ear
        val rightEar = Path().apply {
            moveTo(cx + faceR * 0.85f, cy - faceR * 0.45f)
            lineTo(cx + faceR * 0.75f, cy - faceR * 1.25f)
            lineTo(cx + faceR * 0.20f, cy - faceR * 0.95f)
        }
        scope.drawPath(rightEar, color, style = stroke)

        // Big friendly eyes
        val eyeOffsetY = cy - faceR * 0.15f
        val eyeSpacing = faceR * 0.42f
        scope.drawCircle(color, faceR * 0.12f, Offset(cx - eyeSpacing, eyeOffsetY), style = stroke)
        scope.drawCircle(color, faceR * 0.05f, Offset(cx - eyeSpacing - 5f, eyeOffsetY - 5f)) // pupil highlight
        scope.drawCircle(color, faceR * 0.12f, Offset(cx + eyeSpacing, eyeOffsetY), style = stroke)
        scope.drawCircle(color, faceR * 0.05f, Offset(cx + eyeSpacing - 5f, eyeOffsetY - 5f))

        // Cute triangular nose
        val nose = Path().apply {
            moveTo(cx - 18f, cy + faceR * 0.15f)
            lineTo(cx + 18f, cy + faceR * 0.15f)
            lineTo(cx, cy + faceR * 0.25f)
            close()
        }
        scope.drawPath(nose, color, style = stroke)

        // Smile
        val mouth = Path().apply {
            moveTo(cx - 30f, cy + faceR * 0.38f)
            quadraticTo(cx - 15f, cy + faceR * 0.48f, cx, cy + faceR * 0.30f)
            quadraticTo(cx + 15f, cy + faceR * 0.48f, cx + 30f, cy + faceR * 0.38f)
        }
        scope.drawPath(mouth, color, style = stroke)

        // Whiskers (left)
        scope.drawLine(color, Offset(cx - faceR * 0.35f, cy + faceR * 0.18f), Offset(cx - faceR * 1.15f, cy + faceR * 0.10f), strokeWidth = thin.width)
        scope.drawLine(color, Offset(cx - faceR * 0.35f, cy + faceR * 0.28f), Offset(cx - faceR * 1.15f, cy + faceR * 0.35f), strokeWidth = thin.width)

        // Whiskers (right)
        scope.drawLine(color, Offset(cx + faceR * 0.35f, cy + faceR * 0.18f), Offset(cx + faceR * 1.15f, cy + faceR * 0.10f), strokeWidth = thin.width)
        scope.drawLine(color, Offset(cx + faceR * 0.35f, cy + faceR * 0.28f), Offset(cx + faceR * 1.15f, cy + faceR * 0.35f), strokeWidth = thin.width)

        // Bow Tie below head
        val bowY = cy + faceR + 25f
        val bow = Path().apply {
            moveTo(cx, bowY)
            lineTo(cx - 50f, bowY - 25f)
            lineTo(cx - 50f, bowY + 25f)
            lineTo(cx, bowY)
            lineTo(cx + 50f, bowY - 25f)
            lineTo(cx + 50f, bowY + 25f)
            close()
        }
        scope.drawPath(bow, color, style = stroke)
        scope.drawCircle(color, 14f, Offset(cx, bowY))

        // Cute Yarn Ball in corner
        val yarnX = w * 0.25f
        val yarnY = h * 0.85f
        scope.drawCircle(color, 45f, Offset(yarnX, yarnY), style = stroke)
        val yarnThread = Path().apply {
            moveTo(yarnX + 40f, yarnY + 10f)
            quadraticTo(yarnX + 90f, yarnY + 30f, yarnX + 130f, yarnY - 10f)
            quadraticTo(yarnX + 170f, yarnY - 40f, yarnX + 210f, yarnY + 20f)
        }
        scope.drawPath(yarnThread, color, style = thin)
    }

    private fun drawDino(
        scope: DrawScope,
        w: Float,
        h: Float,
        color: Color,
        stroke: Stroke,
        thin: Stroke
    ) {
        val cx = w * 0.5f
        val cy = h * 0.55f

        // Friendly dino body & long neck path
        val dinoPath = Path().apply {
            // Head
            moveTo(cx - 100f, cy - 250f)
            cubicTo(cx - 40f, cy - 260f, cx + 10f, cy - 230f, cx - 10f, cy - 190f)
            // Mouth
            lineTo(cx - 70f, cy - 190f)
            // Jaw & neck front
            cubicTo(cx - 60f, cy - 150f, cx - 40f, cy - 60f, cx - 130f, cy)
            // Front leg
            lineTo(cx - 130f, cy + 180f)
            lineTo(cx - 80f, cy + 180f)
            lineTo(cx - 80f, cy + 60f)
            // Belly
            cubicTo(cx - 20f, cy + 80f, cx + 50f, cy + 80f, cx + 100f, cy + 60f)
            // Back leg
            lineTo(cx + 100f, cy + 180f)
            lineTo(cx + 150f, cy + 180f)
            lineTo(cx + 150f, cy + 20f)
            // Tail
            cubicTo(cx + 220f, cy - 20f, cx + 280f, cy - 100f, cx + 260f, cy - 140f)
            cubicTo(cx + 220f, cy - 80f, cx + 160f, cy - 20f, cx + 90f, cy - 40f)
            // Back of neck
            cubicTo(cx + 40f, cy - 120f, cx + 10f, cy - 210f, cx - 100f, cy - 250f)
            close()
        }
        scope.drawPath(dinoPath, color, style = stroke)

        // Dino Eye
        scope.drawCircle(color, 12f, Offset(cx - 60f, cy - 225f), style = stroke)
        scope.drawCircle(color, 4f, Offset(cx - 62f, cy - 227f))

        // Dino friendly smile
        val smile = Path().apply {
            moveTo(cx - 95f, cy - 200f)
            quadraticTo(cx - 80f, cy - 190f, cx - 65f, cy - 200f)
        }
        scope.drawPath(smile, color, style = thin)

        // Spikes on back
        for (i in 0..4) {
            val spikeX = cx - 20f + i * 40f
            val spikeY = cy - 70f + (i * i * 3f)
            val spike = Path().apply {
                moveTo(spikeX - 12f, spikeY)
                lineTo(spikeX, spikeY - 26f)
                lineTo(spikeX + 12f, spikeY)
            }
            scope.drawPath(spike, color, style = thin)
        }

        // Cute spots
        scope.drawCircle(color, 14f, Offset(cx + 10f, cy), style = thin)
        scope.drawCircle(color, 10f, Offset(cx + 50f, cy - 10f), style = thin)
        scope.drawCircle(color, 16f, Offset(cx + 70f, cy + 25f), style = thin)

        // Smiling Sun in upper corner
        val sunX = w * 0.8f
        val sunY = h * 0.18f
        scope.drawCircle(color, 36f, Offset(sunX, sunY), style = stroke)
        for (a in 0 until 8) {
            val angle = Math.toRadians((a * 45).toDouble())
            val x1 = sunX + (45f * Math.cos(angle)).toFloat()
            val y1 = sunY + (45f * Math.sin(angle)).toFloat()
            val x2 = sunX + (60f * Math.cos(angle)).toFloat()
            val y2 = sunY + (60f * Math.sin(angle)).toFloat()
            scope.drawLine(color, Offset(x1, y1), Offset(x2, y2), strokeWidth = thin.width)
        }
    }

    private fun drawRocket(
        scope: DrawScope,
        w: Float,
        h: Float,
        color: Color,
        stroke: Stroke,
        thin: Stroke
    ) {
        val cx = w * 0.5f
        val cy = h * 0.45f

        // Rocket Body
        val body = Path().apply {
            moveTo(cx, cy - 240f)
            cubicTo(cx + 80f, cy - 140f, cx + 90f, cy + 80f, cx + 70f, cy + 140f)
            lineTo(cx - 70f, cy + 140f)
            cubicTo(cx - 90f, cy + 80f, cx - 80f, cy - 140f, cx, cy - 240f)
            close()
        }
        scope.drawPath(body, color, style = stroke)

        // Nosecone line
        val noseSep = Path().apply {
            moveTo(cx - 50f, cy - 150f)
            quadraticTo(cx, cy - 130f, cx + 50f, cy - 150f)
        }
        scope.drawPath(noseSep, color, style = stroke)

        // Round Porthole Window
        scope.drawCircle(color, 46f, Offset(cx, cy - 30f), style = stroke)
        scope.drawCircle(color, 36f, Offset(cx, cy - 30f), style = thin)

        // Left Fin
        val leftFin = Path().apply {
            moveTo(cx - 70f, cy + 70f)
            lineTo(cx - 150f, cy + 170f)
            lineTo(cx - 70f, cy + 140f)
            close()
        }
        scope.drawPath(leftFin, color, style = stroke)

        // Right Fin
        val rightFin = Path().apply {
            moveTo(cx + 70f, cy + 70f)
            lineTo(cx + 150f, cy + 170f)
            lineTo(cx + 70f, cy + 140f)
            close()
        }
        scope.drawPath(rightFin, color, style = stroke)

        // Exhaust Fire / Blast
        val fire = Path().apply {
            moveTo(cx - 40f, cy + 140f)
            lineTo(cx - 60f, cy + 220f)
            lineTo(cx - 20f, cy + 190f)
            lineTo(cx, cy + 250f)
            lineTo(cx + 20f, cy + 190f)
            lineTo(cx + 60f, cy + 220f)
            lineTo(cx + 40f, cy + 140f)
        }
        scope.drawPath(fire, color, style = stroke)

        // Crescent Moon in corner
        val moonX = w * 0.22f
        val moonY = h * 0.18f
        val moon = Path().apply {
            moveTo(moonX, moonY - 45f)
            cubicTo(moonX + 50f, moonY - 30f, moonX + 50f, moonY + 30f, moonX, moonY + 45f)
            cubicTo(moonX + 25f, moonY + 25f, moonX + 25f, moonY - 25f, moonX, moonY - 45f)
            close()
        }
        scope.drawPath(moon, color, style = stroke)

        // Twinkling Star
        drawStarHelper(scope, Offset(w * 0.82f, h * 0.22f), 30f, color, stroke)
        drawStarHelper(scope, Offset(w * 0.85f, h * 0.65f), 22f, color, thin)
        drawStarHelper(scope, Offset(w * 0.18f, h * 0.72f), 25f, color, thin)
    }

    private fun drawOcean(
        scope: DrawScope,
        w: Float,
        h: Float,
        color: Color,
        stroke: Stroke,
        thin: Stroke
    ) {
        val cx = w * 0.5f
        val cy = h * 0.48f

        // Friendly Sea Turtle Shell
        scope.drawOval(color, Offset(cx - 120f, cy - 90f), Size(240f, 180f), style = stroke)

        // Shell pattern
        val pattern = Path().apply {
            moveTo(cx - 60f, cy - 20f)
            lineTo(cx, cy - 60f)
            lineTo(cx + 60f, cy - 20f)
            lineTo(cx + 60f, cy + 30f)
            lineTo(cx, cy + 60f)
            lineTo(cx - 60f, cy + 30f)
            close()
        }
        scope.drawPath(pattern, color, style = thin)

        // Head
        val head = Path().apply {
            moveTo(cx + 110f, cy - 35f)
            cubicTo(cx + 170f, cy - 50f, cx + 195f, cy, cx + 180f, cy + 25f)
            cubicTo(cx + 160f, cy + 50f, cx + 120f, cy + 40f, cx + 110f, cy + 25f)
        }
        scope.drawPath(head, color, style = stroke)
        scope.drawCircle(color, 6f, Offset(cx + 160f, cy - 10f)) // eye

        // Flippers
        val frontFlipper = Path().apply {
            moveTo(cx + 70f, cy - 70f)
            cubicTo(cx + 110f, cy - 140f, cx + 40f, cy - 180f, cx, cy - 120f)
            lineTo(cx + 20f, cy - 80f)
        }
        scope.drawPath(frontFlipper, color, style = stroke)

        val bottomFlipper = Path().apply {
            moveTo(cx + 70f, cy + 70f)
            cubicTo(cx + 110f, cy + 140f, cx + 40f, cy + 180f, cx, cy + 120f)
            lineTo(cx + 20f, cy + 80f)
        }
        scope.drawPath(bottomFlipper, color, style = stroke)

        // Cute bubbles
        scope.drawCircle(color, 20f, Offset(cx + 195f, cy - 70f), style = thin)
        scope.drawCircle(color, 14f, Offset(cx + 215f, cy - 110f), style = thin)
        scope.drawCircle(color, 9f, Offset(cx + 230f, cy - 145f), style = thin)

        // Starfish on seabed
        drawStarHelper(scope, Offset(w * 0.22f, h * 0.82f), 35f, color, stroke)
    }

    private fun drawCastle(
        scope: DrawScope,
        w: Float,
        h: Float,
        color: Color,
        stroke: Stroke,
        thin: Stroke
    ) {
        val cx = w * 0.5f
        val cy = h * 0.62f

        // Giant Rainbow Arcs across sky
        for (i in 0..3) {
            val r = minOf(w, h) * 0.50f + (i * 22f)
            scope.drawArc(
                color = color,
                startAngle = 190f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = Offset(cx - r, h * 0.40f - r),
                size = Size(r * 2, r * 2),
                style = thin
            )
        }

        // Castle Base & Central Tower
        val towerWidth = 110f
        val towerHeight = 160f
        scope.drawRect(
            color,
            topLeft = Offset(cx - towerWidth / 2, cy - 40f),
            size = Size(towerWidth, towerHeight),
            style = stroke
        )

        // Turret Roof (Triangle)
        val roof = Path().apply {
            moveTo(cx - towerWidth / 2 - 10f, cy - 40f)
            lineTo(cx, cy - 130f)
            lineTo(cx + towerWidth / 2 + 10f, cy - 40f)
            close()
        }
        scope.drawPath(roof, color, style = stroke)

        // Flag on top
        scope.drawLine(color, Offset(cx, cy - 130f), Offset(cx, cy - 170f), strokeWidth = stroke.width)
        val flag = Path().apply {
            moveTo(cx, cy - 170f)
            lineTo(cx + 35f, cy - 155f)
            lineTo(cx, cy - 140f)
            close()
        }
        scope.drawPath(flag, color, style = thin)

        // Side Towers
        val sideW = 65f
        val sideH = 120f
        // Left tower
        scope.drawRect(
            color,
            topLeft = Offset(cx - towerWidth / 2 - sideW, cy),
            size = Size(sideW, sideH),
            style = stroke
        )
        val leftRoof = Path().apply {
            moveTo(cx - towerWidth / 2 - sideW - 8f, cy)
            lineTo(cx - towerWidth / 2 - sideW / 2, cy - 65f)
            lineTo(cx - towerWidth / 2 + 8f, cy)
            close()
        }
        scope.drawPath(leftRoof, color, style = stroke)

        // Right tower
        scope.drawRect(
            color,
            topLeft = Offset(cx + towerWidth / 2, cy),
            size = Size(sideW, sideH),
            style = stroke
        )
        val rightRoof = Path().apply {
            moveTo(cx + towerWidth / 2 - 8f, cy)
            lineTo(cx + towerWidth / 2 + sideW / 2, cy - 65f)
            lineTo(cx + towerWidth / 2 + sideW + 8f, cy)
            close()
        }
        scope.drawPath(rightRoof, color, style = stroke)

        // Arched Gate Door
        val door = Path().apply {
            moveTo(cx - 30f, cy + 120f)
            lineTo(cx - 30f, cy + 50f)
            arcTo(
                rect = Rect(Offset(cx - 30f, cy + 20f), Size(60f, 60f)),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            lineTo(cx + 30f, cy + 120f)
            close()
        }
        scope.drawPath(door, color, style = stroke)
    }

    private fun drawStarHelper(
        scope: DrawScope,
        center: Offset,
        outerR: Float,
        color: Color,
        stroke: Stroke
    ) {
        val innerR = outerR * 0.42f
        val star = Path()
        for (i in 0 until 10) {
            val angle = Math.toRadians((i * 36 - 90).toDouble())
            val r = if (i % 2 == 0) outerR else innerR
            val x = center.x + (r * Math.cos(angle)).toFloat()
            val y = center.y + (r * Math.sin(angle)).toFloat()
            if (i == 0) star.moveTo(x, y) else star.lineTo(x, y)
        }
        star.close()
        scope.drawPath(star, color, style = stroke)
    }
}
