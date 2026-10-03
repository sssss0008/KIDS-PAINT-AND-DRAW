package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Paint as AndroidPaint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import com.example.model.BrushType
import com.example.model.CanvasPaperTheme
import com.example.model.ColoringTemplates
import com.example.model.DrawAction
import com.example.model.PatternStyle
import com.example.model.ShapeType
import com.example.model.SymmetryMode
import com.example.viewmodel.DrawingUiState

@Composable
fun KidsCanvas(
    state: DrawingUiState,
    modifier: Modifier = Modifier,
    onTouchDown: (Offset) -> Unit,
    onTouchMove: (Offset) -> Unit,
    onTouchUp: () -> Unit,
    onCanvasSizeChanged: (Int, Int) -> Unit = { _, _ -> },
    replayActionCount: Int? = null
) {
    val textPaint = remember {
        AndroidPaint().apply {
            textAlign = AndroidPaint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(state.paperTheme.bgColor)
            .testTag("kids_drawing_canvas")
            .pointerInput(state.selectedBrush, state.selectedShape, state.selectedStamp, replayActionCount) {
                if (replayActionCount != null) return@pointerInput
                detectTapGestures(
                    onPress = { offset ->
                        onTouchDown(offset)
                        tryAwaitRelease()
                        onTouchUp()
                    }
                )
            }
            .pointerInput(state.selectedBrush, state.selectedShape, state.selectedStamp, replayActionCount) {
                if (replayActionCount != null) return@pointerInput
                detectDragGestures(
                    onDragStart = { offset -> onTouchDown(offset) },
                    onDrag = { change, _ ->
                        change.consume()
                        onTouchMove(change.position)
                    },
                    onDragEnd = { onTouchUp() },
                    onDragCancel = { onTouchUp() }
                )
            }
    ) {
        val w = size.width
        val h = size.height
        if (w > 0 && h > 0) {
            onCanvasSizeChanged(w.toInt(), h.toInt())
        }

        // 1. Draw Paper Texture Background
        renderPaperBackground(this, state.paperTheme)

        // 2. Draw coloring page template if active
        state.activeTemplateId?.let { templateId ->
            ColoringTemplates.drawTemplate(this, templateId)
        }

        // 3. Render completed drawing actions (support replay count)
        val actionsToRender = if (replayActionCount != null) {
            state.actions.take(replayActionCount)
        } else {
            state.actions
        }

        for (action in actionsToRender) {
            renderAction(this, action, state.symmetryMode, textPaint)
        }

        // 4. Render currently active live stroke or shape preview (only if not replaying)
        if (replayActionCount == null) {
            state.currentAction?.let { liveAction ->
                renderAction(this, liveAction, state.symmetryMode, textPaint)
            }
        }
    }
}

fun renderPaperBackground(scope: DrawScope, theme: CanvasPaperTheme) {
    val w = scope.size.width
    val h = scope.size.height

    when (theme) {
        CanvasPaperTheme.GRID_NOTEBOOK -> {
            // Draw clean light blue grid lines
            val step = 32f
            var x = step
            val gridColor = Color(0x223B82F6)
            while (x < w) {
                scope.drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                x += step
            }
            var y = step
            while (y < h) {
                scope.drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                y += step
            }
        }
        CanvasPaperTheme.STARRY_NIGHT -> {
            // Draw subtle distant background stars
            val starColor = Color(0x66FFFFFF)
            val count = 28
            for (i in 0 until count) {
                val sx = ((i * 137.5f) % w)
                val sy = ((i * 223.3f) % h)
                val r = if (i % 3 == 0) 2.2f else 1.2f
                scope.drawCircle(starColor, r, Offset(sx, sy))
            }
        }
        CanvasPaperTheme.CHALKBOARD -> {
            // Draw faint chalk dust streaks
            val dustColor = Color(0x0CFFFFFF)
            scope.drawRect(dustColor, topLeft = Offset(10f, 10f), size = Size(w - 20f, h - 20f))
        }
        CanvasPaperTheme.VINTAGE_PARCHMENT -> {
            // Subtle parchment border vignette
            scope.drawRect(
                Color(0x1578350F),
                topLeft = Offset(0f, 0f),
                size = Size(w, h),
                style = Stroke(width = 16f)
            )
        }
        CanvasPaperTheme.BLANK_WHITE, CanvasPaperTheme.PASTEL_PINK -> {
            // Clean solid
        }
    }
}

fun renderAction(
    scope: DrawScope,
    action: DrawAction,
    symmetryMode: SymmetryMode,
    textPaint: AndroidPaint
) {
    when (action) {
        is DrawAction.FreehandStroke -> {
            renderFreehandStroke(scope, action, symmetryMode, textPaint)
        }
        is DrawAction.ShapeStroke -> {
            renderShapeStroke(scope, action, symmetryMode)
        }
        is DrawAction.StampAction -> {
            renderStampAction(scope, action, symmetryMode, textPaint)
        }
        is DrawAction.FloodFillAction -> {
            // Flood fill paints the entire canvas or targeted area with that color
            scope.drawRect(
                color = action.color,
                topLeft = Offset.Zero,
                size = scope.size
            )
        }
    }
}

private fun renderFreehandStroke(
    scope: DrawScope,
    stroke: DrawAction.FreehandStroke,
    symmetryMode: SymmetryMode,
    textPaint: AndroidPaint
) {
    if (stroke.points.isEmpty()) return

    val w = scope.size.width
    val h = scope.size.height

    fun drawPoints(points: List<Offset>) {
        if (points.isEmpty()) return
        if (points.size == 1) {
            val dotColor = if (stroke.brushType == BrushType.MAGIC_RAINBOW) Color(0xFFFF4081) else stroke.color
            scope.drawCircle(dotColor, stroke.strokeWidth / 2f, points.first())
            return
        }

        when (stroke.brushType) {
            BrushType.MAGIC_RAINBOW -> {
                for (i in 0 until points.size - 1) {
                    val p1 = points[i]
                    val p2 = points[i + 1]
                    val hue = (i * 14f) % 360f
                    val segColor = Color.hsv(hue, 0.95f, 1f)
                    scope.drawLine(
                        color = segColor,
                        start = p1,
                        end = p2,
                        strokeWidth = stroke.strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
            BrushType.NEON_GLOW -> {
                val path = buildSmoothPath(points)
                // Outer glow
                scope.drawPath(
                    path = path,
                    color = stroke.color.copy(alpha = 0.25f),
                    style = Stroke(width = stroke.strokeWidth * 2.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                // Mid glow
                scope.drawPath(
                    path = path,
                    color = stroke.color.copy(alpha = 0.60f),
                    style = Stroke(width = stroke.strokeWidth * 1.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                // Intense bright core
                scope.drawPath(
                    path = path,
                    color = Color.White.copy(alpha = 0.95f),
                    style = Stroke(width = stroke.strokeWidth * 0.65f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
            BrushType.BUBBLES -> {
                // Draws playful translucent iridescent bubbles
                for (i in points.indices step 3) {
                    val pt = points[i]
                    val bubbleR = (stroke.strokeWidth * 0.9f) + ((i % 5) * 4f)
                    val bubbleColor = stroke.color.copy(alpha = 0.45f)
                    // Translucent bubble body
                    scope.drawCircle(bubbleColor, bubbleR, pt)
                    // Bubble outline
                    scope.drawCircle(stroke.color.copy(alpha = 0.85f), bubbleR, pt, style = Stroke(width = 2.5f))
                    // Specular highlight reflection
                    scope.drawArc(
                        color = Color.White.copy(alpha = 0.9f),
                        startAngle = 210f,
                        sweepAngle = 70f,
                        useCenter = false,
                        topLeft = Offset(pt.x - bubbleR * 0.75f, pt.y - bubbleR * 0.75f),
                        size = Size(bubbleR * 1.5f, bubbleR * 1.5f),
                        style = Stroke(width = 3f, cap = StrokeCap.Round)
                    )
                }
            }
            BrushType.FIREWORKS -> {
                // Radiating colorful spark bursts along path
                val path = buildSmoothPath(points)
                scope.drawPath(
                    path = path,
                    color = stroke.color.copy(alpha = 0.4f),
                    style = Stroke(width = stroke.strokeWidth * 0.4f, cap = StrokeCap.Round)
                )
                for (i in points.indices step 4) {
                    val pt = points[i]
                    val sparkLen = stroke.strokeWidth * 1.4f
                    for (angleDeg in 0 until 360 step 45) {
                        val rad = Math.toRadians(angleDeg.toDouble())
                        val endX = pt.x + (sparkLen * Math.cos(rad)).toFloat()
                        val endY = pt.y + (sparkLen * Math.sin(rad)).toFloat()
                        val sparkColor = Color.hsv(((i * 20f + angleDeg) % 360f), 0.9f, 1f)
                        scope.drawLine(sparkColor, pt, Offset(endX, endY), strokeWidth = 2.5f, cap = StrokeCap.Round)
                    }
                    scope.drawCircle(Color.White, 4f, pt)
                }
            }
            BrushType.PATTERN_ROLLER -> {
                // Repeating cute emojis stamped along path
                textPaint.textSize = stroke.strokeWidth * 1.3f
                val emoji = stroke.patternStyle.emoji
                for (i in points.indices step 5) {
                    val pt = points[i]
                    scope.drawContext.canvas.nativeCanvas.drawText(
                        emoji,
                        pt.x,
                        pt.y + stroke.strokeWidth * 0.45f,
                        textPaint
                    )
                }
            }
            BrushType.CHALK -> {
                // Soft chalk texture
                val path = buildSmoothPath(points)
                scope.drawPath(
                    path = path,
                    color = stroke.color.copy(alpha = 0.65f),
                    style = Stroke(width = stroke.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                scope.drawPath(
                    path = path,
                    color = Color.White.copy(alpha = 0.35f),
                    style = Stroke(width = stroke.strokeWidth * 0.6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
            BrushType.SPARKLE -> {
                val path = buildSmoothPath(points)
                scope.drawPath(
                    path = path,
                    color = stroke.color,
                    style = Stroke(width = stroke.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                for (i in points.indices step 5) {
                    val pt = points[i]
                    drawGlitterSparkle(scope, pt, stroke.strokeWidth * 0.8f, Color(0xFFFFF176))
                }
            }
            BrushType.CRAYON -> {
                val path = buildSmoothPath(points)
                scope.drawPath(
                    path = path,
                    color = stroke.color.copy(alpha = 0.88f),
                    style = Stroke(width = stroke.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
            BrushType.MARKER, BrushType.ERASER, BrushType.STAMP, BrushType.BUCKET -> {
                val path = buildSmoothPath(points)
                scope.drawPath(
                    path = path,
                    color = stroke.color,
                    style = Stroke(width = stroke.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
    }

    drawPoints(stroke.points)

    if (symmetryMode == SymmetryMode.MIRROR_HORIZONTAL || symmetryMode == SymmetryMode.MIRROR_QUAD) {
        val mirroredX = stroke.points.map { Offset(w - it.x, it.y) }
        drawPoints(mirroredX)
    }

    if (symmetryMode == SymmetryMode.MIRROR_QUAD) {
        val mirroredY = stroke.points.map { Offset(it.x, h - it.y) }
        val mirroredBoth = stroke.points.map { Offset(w - it.x, h - it.y) }
        drawPoints(mirroredY)
        drawPoints(mirroredBoth)
    }
}

private fun renderShapeStroke(
    scope: DrawScope,
    stroke: DrawAction.ShapeStroke,
    symmetryMode: SymmetryMode
) {
    val w = scope.size.width
    val h = scope.size.height

    fun drawSingleShape(start: Offset, end: Offset) {
        val left = minOf(start.x, end.x)
        val top = minOf(start.y, end.y)
        val right = maxOf(start.x, end.x)
        val bottom = maxOf(start.y, end.y)
        val width = maxOf(2f, right - left)
        val height = maxOf(2f, bottom - top)

        val drawStyle = if (stroke.isFilled) Fill else Stroke(
            width = stroke.strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        when (stroke.shapeType) {
            ShapeType.LINE -> {
                scope.drawLine(
                    color = stroke.color,
                    start = start,
                    end = end,
                    strokeWidth = stroke.strokeWidth,
                    cap = StrokeCap.Round
                )
            }
            ShapeType.RECTANGLE -> {
                scope.drawRect(
                    color = stroke.color,
                    topLeft = Offset(left, top),
                    size = Size(width, height),
                    style = drawStyle
                )
            }
            ShapeType.CIRCLE -> {
                scope.drawOval(
                    color = stroke.color,
                    topLeft = Offset(left, top),
                    size = Size(width, height),
                    style = drawStyle
                )
            }
            ShapeType.STAR -> {
                val cx = (left + right) / 2f
                val cy = (top + bottom) / 2f
                val radius = maxOf(width, height) / 2f
                val starPath = Path()
                for (i in 0 until 10) {
                    val angle = Math.toRadians((i * 36 - 90).toDouble())
                    val r = if (i % 2 == 0) radius else radius * 0.45f
                    val x = cx + (r * Math.cos(angle)).toFloat()
                    val y = cy + (r * Math.sin(angle)).toFloat()
                    if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
                }
                starPath.close()
                scope.drawPath(starPath, stroke.color, style = drawStyle)
            }
            ShapeType.HEART -> {
                val cx = (left + right) / 2f
                val heartPath = Path().apply {
                    moveTo(cx, top + height * 0.35f)
                    cubicTo(left, top, left, top + height * 0.7f, cx, bottom)
                    cubicTo(right, top + height * 0.7f, right, top, cx, top + height * 0.35f)
                    close()
                }
                scope.drawPath(heartPath, stroke.color, style = drawStyle)
            }
            ShapeType.NONE -> {}
        }
    }

    drawSingleShape(stroke.start, stroke.end)

    if (symmetryMode == SymmetryMode.MIRROR_HORIZONTAL || symmetryMode == SymmetryMode.MIRROR_QUAD) {
        val mStart = Offset(w - stroke.start.x, stroke.start.y)
        val mEnd = Offset(w - stroke.end.x, stroke.end.y)
        drawSingleShape(mStart, mEnd)
    }

    if (symmetryMode == SymmetryMode.MIRROR_QUAD) {
        val yStart = Offset(stroke.start.x, h - stroke.start.y)
        val yEnd = Offset(stroke.end.x, h - stroke.end.y)
        drawSingleShape(yStart, yEnd)

        val bStart = Offset(w - stroke.start.x, h - stroke.start.y)
        val bEnd = Offset(w - stroke.end.x, h - stroke.end.y)
        drawSingleShape(bStart, bEnd)
    }
}

private fun renderStampAction(
    scope: DrawScope,
    stamp: DrawAction.StampAction,
    symmetryMode: SymmetryMode,
    textPaint: AndroidPaint
) {
    val w = scope.size.width
    val h = scope.size.height

    textPaint.textSize = stamp.size

    fun drawSingleStamp(pos: Offset) {
        scope.drawContext.canvas.nativeCanvas.drawText(
            stamp.emoji,
            pos.x,
            pos.y + stamp.size * 0.35f,
            textPaint
        )
    }

    drawSingleStamp(stamp.position)

    if (symmetryMode == SymmetryMode.MIRROR_HORIZONTAL || symmetryMode == SymmetryMode.MIRROR_QUAD) {
        drawSingleStamp(Offset(w - stamp.position.x, stamp.position.y))
    }

    if (symmetryMode == SymmetryMode.MIRROR_QUAD) {
        drawSingleStamp(Offset(stamp.position.x, h - stamp.position.y))
        drawSingleStamp(Offset(w - stamp.position.x, h - stamp.position.y))
    }
}

private fun buildSmoothPath(points: List<Offset>): Path {
    val path = Path()
    if (points.isEmpty()) return path
    path.moveTo(points.first().x, points.first().y)
    for (i in 1 until points.size) {
        val prev = points[i - 1]
        val cur = points[i]
        val midX = (prev.x + cur.x) / 2f
        val midY = (prev.y + cur.y) / 2f
        path.quadraticTo(prev.x, prev.y, midX, midY)
    }
    path.lineTo(points.last().x, points.last().y)
    return path
}

private fun drawGlitterSparkle(scope: DrawScope, pt: Offset, size: Float, color: Color) {
    val half = size / 2f
    scope.drawLine(color, Offset(pt.x - half, pt.y), Offset(pt.x + half, pt.y), strokeWidth = 2.5f)
    scope.drawLine(color, Offset(pt.x, pt.y - half), Offset(pt.x, pt.y + half), strokeWidth = 2.5f)
    scope.drawCircle(Color.White, size * 0.2f, pt)
}

fun exportDrawingToBitmap(
    state: DrawingUiState,
    width: Int,
    height: Int
): Bitmap {
    val validW = if (width <= 0) 1080 else width
    val validH = if (height <= 0) 1440 else height
    val bitmap = Bitmap.createBitmap(validW, validH, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    // Fill background
    canvas.drawColor(state.paperTheme.bgColor.toArgb())

    val composeCanvas = androidx.compose.ui.graphics.Canvas(canvas)
    val drawScope = androidx.compose.ui.graphics.drawscope.CanvasDrawScope()
    val size = Size(validW.toFloat(), validH.toFloat())
    val textPaint = AndroidPaint().apply {
        textAlign = AndroidPaint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    drawScope.draw(
        density = androidx.compose.ui.unit.Density(1f),
        layoutDirection = androidx.compose.ui.unit.LayoutDirection.Ltr,
        canvas = composeCanvas,
        size = size
    ) {
        renderPaperBackground(this, state.paperTheme)
        state.activeTemplateId?.let { templateId ->
            ColoringTemplates.drawTemplate(this, templateId)
        }
        for (action in state.actions) {
            renderAction(this, action, state.symmetryMode, textPaint)
        }
    }

    return bitmap
}
