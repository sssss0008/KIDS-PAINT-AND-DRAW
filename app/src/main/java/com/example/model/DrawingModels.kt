package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

enum class BrushType(val displayName: String, val iconEmoji: String) {
    CRAYON("Crayon", "🖍️"),
    MARKER("Marker", "🖊️"),
    MAGIC_RAINBOW("Rainbow", "🌈"),
    NEON_GLOW("Neon", "✨"),
    SPARKLE("Glitter", "🌟"),
    BUBBLES("Bubbles", "🫧"),
    FIREWORKS("Fireworks", "🎆"),
    PATTERN_ROLLER("Pattern", "🪩"),
    CHALK("Chalk", "🪵"),
    BUCKET("Fill", "🪣"),
    STAMP("Sticker", "⭐"),
    ERASER("Eraser", "🧽")
}

enum class PatternStyle(val displayName: String, val emoji: String) {
    HEARTS("Hearts", "💖"),
    STARS("Stars", "⭐"),
    PAW_PRINTS("Paws", "🐾"),
    CANDY("Candy", "🍬"),
    FLOWERS("Flowers", "🌸"),
    MUSIC_NOTES("Music", "🎵")
}

enum class CanvasPaperTheme(
    val displayName: String,
    val iconEmoji: String,
    val bgColor: Color,
    val defaultStrokeColor: Color
) {
    BLANK_WHITE("Clean Paper", "📄", Color(0xFFFFFFFF), Color(0xFFFF1744)),
    CHALKBOARD("Chalkboard", "⬛", Color(0xFF1E293B), Color(0xFF38BDF8)),
    STARRY_NIGHT("Space Galaxy", "🌌", Color(0xFF0B0F19), Color(0xFFFFD600)),
    GRID_NOTEBOOK("Grid Paper", "📐", Color(0xFFF8FAFC), Color(0xFF2563EB)),
    VINTAGE_PARCHMENT("Parchment", "📜", Color(0xFFFAF0E6), Color(0xFF78350F)),
    PASTEL_PINK("Cotton Candy", "🍧", Color(0xFFFFF1F2), Color(0xFFBE185D))
}

enum class ShapeType(val displayName: String, val iconEmoji: String) {
    NONE("Freehand", "✏️"),
    LINE("Line", "📏"),
    RECTANGLE("Box", "🟦"),
    CIRCLE("Circle", "⚪"),
    STAR("Star", "⭐"),
    HEART("Heart", "❤️")
}

enum class SymmetryMode(val displayName: String, val iconEmoji: String) {
    OFF("Normal", "🖌️"),
    MIRROR_HORIZONTAL("Mirror 2x", "🦋"),
    MIRROR_QUAD("Kaleidoscope 4x", "❄️")
}

sealed class DrawAction {
    data class FreehandStroke(
        val points: List<Offset>,
        val color: Color,
        val strokeWidth: Float,
        val brushType: BrushType,
        val patternStyle: PatternStyle = PatternStyle.STARS
    ) : DrawAction()

    data class ShapeStroke(
        val start: Offset,
        val end: Offset,
        val color: Color,
        val strokeWidth: Float,
        val shapeType: ShapeType,
        val isFilled: Boolean
    ) : DrawAction()

    data class StampAction(
        val position: Offset,
        val emoji: String,
        val size: Float,
        val rotation: Float = 0f
    ) : DrawAction()

    data class FloodFillAction(
        val point: Offset,
        val color: Color
    ) : DrawAction()
}

data class SavedDrawing(
    val id: String,
    val title: String,
    val filePath: String,
    val dateText: String,
    val timestamp: Long,
    val actionsCount: Int = 0
)

data class ColoringPage(
    val id: String,
    val title: String,
    val emoji: String,
    val description: String,
    val category: String
)

data class DrawingPrompt(
    val text: String,
    val emoji: String,
    val category: String
)

data class KidBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean,
    val progress: Int,
    val target: Int
)
