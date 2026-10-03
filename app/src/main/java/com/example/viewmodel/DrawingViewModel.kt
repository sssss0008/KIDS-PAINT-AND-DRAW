package com.example.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.content.Intent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundPlayer
import com.example.data.DrawingRepository
import com.example.model.BrushType
import com.example.model.CanvasPaperTheme
import com.example.model.DrawAction
import com.example.model.KidBadge
import com.example.model.KidQuestsAndBadges
import com.example.model.PatternStyle
import com.example.model.SavedDrawing
import com.example.model.ShapeType
import com.example.model.SymmetryMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppScreen {
    CANVAS,
    COLORING_BOOK,
    GALLERY
}

data class DrawingUiState(
    val currentScreen: AppScreen = AppScreen.CANVAS,
    val actions: List<DrawAction> = emptyList(),
    val redoStack: List<DrawAction> = emptyList(),
    val currentAction: DrawAction? = null,
    val selectedBrush: BrushType = BrushType.CRAYON,
    val selectedColor: Color = Color(0xFFFF1744),
    val strokeWidth: Float = 22f,
    val selectedShape: ShapeType = ShapeType.NONE,
    val isShapeFilled: Boolean = false,
    val selectedStamp: String = "⭐",
    val stampSize: Float = 60f,
    val selectedPattern: PatternStyle = PatternStyle.STARS,
    val symmetryMode: SymmetryMode = SymmetryMode.OFF,
    val paperTheme: CanvasPaperTheme = CanvasPaperTheme.BLANK_WHITE,
    val activeTemplateId: String? = null,
    val activeTemplateTitle: String? = null,
    val savedDrawings: List<SavedDrawing> = emptyList(),
    val badges: List<KidBadge> = KidQuestsAndBadges.getInitialBadges(),
    val isSaving: Boolean = false,
    val showConfetti: Boolean = false,
    val isSoundMuted: Boolean = false,
    val showClearDialog: Boolean = false,
    val showColorPickerSheet: Boolean = false,
    val showStampSheet: Boolean = false,
    val showShapeSheet: Boolean = false,
    val showSizeSheet: Boolean = false,
    val showPaperThemeSheet: Boolean = false,
    val showPatternSheet: Boolean = false,
    val showPromptDialog: Boolean = false,
    val showBadgesDialog: Boolean = false,
    val showReplayDialog: Boolean = false,
    val fullscreenDrawing: SavedDrawing? = null,
    val parentalGateChallenge: Pair<String, Int>? = null
)

class DrawingViewModel(
    private val repository: DrawingRepository,
    private val context: Context? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(DrawingUiState())
    val uiState: StateFlow<DrawingUiState> = _uiState.asStateFlow()

    private var pendingStrokePoints = mutableListOf<Offset>()
    private var shapeStartOffset: Offset? = null
    private var pendingParentalSuccess: (() -> Unit)? = null

    // Badges counters
    private var rainbowCount = 0
    private var stickerCount = 0
    private var bubbleCount = 0
    private var fireworkCount = 0
    private var shapeCount = 0

    init {
        loadGallery()
        loadBadgesFromPrefs()
    }

    fun navigateTo(screen: AppScreen) {
        SoundPlayer.playPop()
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun loadGallery() {
        viewModelScope.launch {
            val list = repository.getSavedDrawings()
            _uiState.update { it.copy(savedDrawings = list) }
        }
    }

    fun toggleSound() {
        val newMuted = !_uiState.value.isSoundMuted
        SoundPlayer.isMuted = newMuted
        _uiState.update { it.copy(isSoundMuted = newMuted) }
        if (!newMuted) {
            SoundPlayer.playPop()
        }
    }

    fun setBrush(brush: BrushType) {
        when (brush) {
            BrushType.BUBBLES -> SoundPlayer.playBubble()
            BrushType.FIREWORKS -> SoundPlayer.playFirework()
            BrushType.CHALK -> SoundPlayer.playChalk()
            BrushType.BUCKET -> SoundPlayer.playFill()
            BrushType.PATTERN_ROLLER -> {
                SoundPlayer.playPop()
                setPickerSheet(pattern = true)
            }
            else -> SoundPlayer.playPop()
        }

        _uiState.update {
            it.copy(
                selectedBrush = brush,
                selectedShape = ShapeType.NONE
            )
        }
    }

    fun setColor(color: Color) {
        SoundPlayer.playPop()
        _uiState.update { it.copy(selectedColor = color, showColorPickerSheet = false) }
    }

    fun setStrokeWidth(width: Float) {
        SoundPlayer.playPop()
        _uiState.update { it.copy(strokeWidth = width, showSizeSheet = false) }
    }

    fun setShape(shape: ShapeType, filled: Boolean) {
        SoundPlayer.playPop()
        _uiState.update {
            it.copy(
                selectedShape = shape,
                isShapeFilled = filled,
                showShapeSheet = false
            )
        }
    }

    fun setStamp(stamp: String) {
        SoundPlayer.playBoing()
        _uiState.update {
            it.copy(
                selectedBrush = BrushType.STAMP,
                selectedShape = ShapeType.NONE,
                selectedStamp = stamp,
                showStampSheet = false
            )
        }
    }

    fun setPattern(pattern: PatternStyle) {
        SoundPlayer.playSparkle()
        _uiState.update {
            it.copy(
                selectedBrush = BrushType.PATTERN_ROLLER,
                selectedPattern = pattern,
                showPatternSheet = false
            )
        }
    }

    fun setPaperTheme(theme: CanvasPaperTheme) {
        SoundPlayer.playPop()
        if (theme == CanvasPaperTheme.CHALKBOARD) {
            incrementBadge("chalk_crafter", 1)
        }
        _uiState.update {
            it.copy(
                paperTheme = theme,
                selectedColor = theme.defaultStrokeColor,
                showPaperThemeSheet = false
            )
        }
    }

    fun cycleSymmetry() {
        SoundPlayer.playPop()
        val next = when (_uiState.value.symmetryMode) {
            SymmetryMode.OFF -> SymmetryMode.MIRROR_HORIZONTAL
            SymmetryMode.MIRROR_HORIZONTAL -> SymmetryMode.MIRROR_QUAD
            SymmetryMode.MIRROR_QUAD -> SymmetryMode.OFF
        }
        _uiState.update { it.copy(symmetryMode = next) }
    }

    fun selectColoringTemplate(templateId: String?, title: String?) {
        SoundPlayer.playSparkle()
        _uiState.update {
            it.copy(
                activeTemplateId = templateId,
                activeTemplateTitle = title,
                actions = emptyList(),
                redoStack = emptyList(),
                currentScreen = AppScreen.CANVAS
            )
        }
    }

    fun acceptDailyPrompt(promptText: String) {
        SoundPlayer.playCelebration()
        _uiState.update {
            it.copy(
                activeTemplateTitle = promptText,
                showPromptDialog = false,
                currentScreen = AppScreen.CANVAS
            )
        }
    }

    fun onTouchDown(point: Offset) {
        val state = _uiState.value
        when {
            state.selectedBrush == BrushType.BUCKET -> {
                // Flood Fill
                SoundPlayer.playFill()
                val fillAction = DrawAction.FloodFillAction(
                    point = point,
                    color = state.selectedColor
                )
                _uiState.update {
                    it.copy(
                        actions = it.actions + fillAction,
                        redoStack = emptyList()
                    )
                }
            }
            state.selectedBrush == BrushType.STAMP -> {
                SoundPlayer.playBoing()
                stickerCount++
                incrementBadge("sticker_star", 1)
                val stampAction = DrawAction.StampAction(
                    position = point,
                    emoji = state.selectedStamp,
                    size = state.stampSize
                )
                _uiState.update {
                    it.copy(
                        actions = it.actions + stampAction,
                        redoStack = emptyList()
                    )
                }
            }
            state.selectedShape != ShapeType.NONE -> {
                shapeStartOffset = point
                _uiState.update {
                    it.copy(
                        currentAction = DrawAction.ShapeStroke(
                            start = point,
                            end = point,
                            color = state.selectedColor,
                            strokeWidth = state.strokeWidth,
                            shapeType = state.selectedShape,
                            isFilled = state.isShapeFilled
                        )
                    )
                }
            }
            else -> {
                // Freehand
                pendingStrokePoints = mutableListOf(point)
                val isEraser = state.selectedBrush == BrushType.ERASER
                val strokeColor = if (isEraser) state.paperTheme.bgColor else state.selectedColor
                val strokeW = if (isEraser) state.strokeWidth * 1.8f else state.strokeWidth

                _uiState.update {
                    it.copy(
                        currentAction = DrawAction.FreehandStroke(
                            points = pendingStrokePoints.toList(),
                            color = strokeColor,
                            strokeWidth = strokeW,
                            brushType = state.selectedBrush,
                            patternStyle = state.selectedPattern
                        )
                    )
                }
            }
        }
    }

    fun onTouchMove(point: Offset) {
        val state = _uiState.value
        when {
            state.selectedBrush == BrushType.BUCKET || state.selectedBrush == BrushType.STAMP -> {
                // Tap-only actions
            }
            state.selectedShape != ShapeType.NONE -> {
                val start = shapeStartOffset ?: point
                _uiState.update {
                    it.copy(
                        currentAction = DrawAction.ShapeStroke(
                            start = start,
                            end = point,
                            color = state.selectedColor,
                            strokeWidth = state.strokeWidth,
                            shapeType = state.selectedShape,
                            isFilled = state.isShapeFilled
                        )
                    )
                }
            }
            else -> {
                if (pendingStrokePoints.isEmpty() || (point - pendingStrokePoints.last()).getDistanceSquared() > 4f) {
                    pendingStrokePoints.add(point)
                    val isEraser = state.selectedBrush == BrushType.ERASER
                    val strokeColor = if (isEraser) state.paperTheme.bgColor else state.selectedColor
                    val strokeW = if (isEraser) state.strokeWidth * 1.8f else state.strokeWidth

                    _uiState.update {
                        it.copy(
                            currentAction = DrawAction.FreehandStroke(
                                points = pendingStrokePoints.toList(),
                                color = strokeColor,
                                strokeWidth = strokeW,
                                brushType = state.selectedBrush,
                                patternStyle = state.selectedPattern
                            )
                        )
                    }
                }
            }
        }
    }

    fun onTouchUp() {
        val action = _uiState.value.currentAction
        if (action != null) {
            when (_uiState.value.selectedBrush) {
                BrushType.MAGIC_RAINBOW -> {
                    SoundPlayer.playSparkle()
                    rainbowCount++
                    incrementBadge("rainbow_master", 1)
                }
                BrushType.BUBBLES -> {
                    SoundPlayer.playBubble()
                    bubbleCount++
                    incrementBadge("bubble_blower", 1)
                }
                BrushType.FIREWORKS -> {
                    SoundPlayer.playFirework()
                    fireworkCount++
                    incrementBadge("fireworks_hero", 1)
                }
                BrushType.CHALK -> SoundPlayer.playChalk()
                BrushType.SPARKLE -> SoundPlayer.playSparkle()
                else -> SoundPlayer.playPop()
            }

            if (_uiState.value.selectedShape != ShapeType.NONE) {
                shapeCount++
                incrementBadge("shape_genius", 1)
            }

            _uiState.update {
                it.copy(
                    actions = it.actions + action,
                    redoStack = emptyList(),
                    currentAction = null
                )
            }
        }
        pendingStrokePoints.clear()
        shapeStartOffset = null
    }

    fun undo() {
        val state = _uiState.value
        if (state.actions.isNotEmpty()) {
            SoundPlayer.playWhoosh()
            val lastAction = state.actions.last()
            val newActions = state.actions.dropLast(1)
            _uiState.update {
                it.copy(
                    actions = newActions,
                    redoStack = it.redoStack + lastAction
                )
            }
        }
    }

    fun redo() {
        val state = _uiState.value
        if (state.redoStack.isNotEmpty()) {
            SoundPlayer.playPop()
            val actionToRedo = state.redoStack.last()
            val newRedo = state.redoStack.dropLast(1)
            _uiState.update {
                it.copy(
                    actions = it.actions + actionToRedo,
                    redoStack = newRedo
                )
            }
        }
    }

    fun showClearConfirm(show: Boolean) {
        if (show) SoundPlayer.playPop()
        _uiState.update { it.copy(showClearDialog = show) }
    }

    fun clearCanvas() {
        SoundPlayer.playSplash()
        _uiState.update {
            it.copy(
                actions = emptyList(),
                redoStack = emptyList(),
                currentAction = null,
                showClearDialog = false
            )
        }
    }

    fun saveArtwork(bitmap: Bitmap, customTitle: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val saved = repository.saveDrawing(bitmap, customTitle)
                val updatedList = repository.getSavedDrawings()
                incrementBadge("first_art", 1)
                SoundPlayer.playCelebration()
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        savedDrawings = updatedList,
                        showConfetti = true
                    )
                }
                delay(3500)
                _uiState.update { it.copy(showConfetti = false) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun deleteDrawing(id: String) {
        viewModelScope.launch {
            SoundPlayer.playWhoosh()
            repository.deleteDrawing(id)
            val updated = repository.getSavedDrawings()
            _uiState.update {
                it.copy(
                    savedDrawings = updated,
                    fullscreenDrawing = if (it.fullscreenDrawing?.id == id) null else it.fullscreenDrawing
                )
            }
        }
    }

    fun openFullscreenDrawing(drawing: SavedDrawing?) {
        if (drawing != null) SoundPlayer.playPop()
        _uiState.update { it.copy(fullscreenDrawing = drawing) }
    }

    fun getShareIntent(drawing: SavedDrawing): Intent? {
        return repository.getShareIntent(drawing)
    }

    // Modal Sheet Toggles
    fun setPickerSheet(
        color: Boolean = false,
        stamp: Boolean = false,
        shape: Boolean = false,
        size: Boolean = false,
        paper: Boolean = false,
        pattern: Boolean = false,
        prompt: Boolean = false,
        badges: Boolean = false,
        replay: Boolean = false
    ) {
        SoundPlayer.playPop()
        if (replay) {
            incrementBadge("movie_director", 1)
        }
        _uiState.update {
            it.copy(
                showColorPickerSheet = color,
                showStampSheet = stamp,
                showShapeSheet = shape,
                showSizeSheet = size,
                showPaperThemeSheet = paper,
                showPatternSheet = pattern,
                showPromptDialog = prompt,
                showBadgesDialog = badges,
                showReplayDialog = replay
            )
        }
    }

    // Badges system
    private fun incrementBadge(badgeId: String, amount: Int) {
        val currentBadges = _uiState.value.badges
        var unlockedNow = false
        val updated = currentBadges.map { badge ->
            if (badge.id == badgeId && !badge.isUnlocked) {
                val newProgress = (badge.progress + amount).coerceAtMost(badge.target)
                val isNowUnlocked = newProgress >= badge.target
                if (isNowUnlocked) unlockedNow = true
                badge.copy(progress = newProgress, isUnlocked = isNowUnlocked)
            } else {
                badge
            }
        }
        _uiState.update { it.copy(badges = updated) }
        saveBadgesToPrefs(updated)
        if (unlockedNow) {
            SoundPlayer.playTrophyUnlock()
        }
    }

    private fun loadBadgesFromPrefs() {
        context?.let { ctx ->
            val prefs = ctx.getSharedPreferences("kids_badges_data", Context.MODE_PRIVATE)
            val initial = KidQuestsAndBadges.getInitialBadges()
            val loaded = initial.map { badge ->
                val progress = prefs.getInt("${badge.id}_prog", 0)
                val unlocked = prefs.getBoolean("${badge.id}_unlocked", false)
                badge.copy(progress = progress, isUnlocked = unlocked)
            }
            _uiState.update { it.copy(badges = loaded) }
        }
    }

    private fun saveBadgesToPrefs(badges: List<KidBadge>) {
        context?.let { ctx ->
            val prefs = ctx.getSharedPreferences("kids_badges_data", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            badges.forEach { badge ->
                editor.putInt("${badge.id}_prog", badge.progress)
                editor.putBoolean("${badge.id}_unlocked", badge.isUnlocked)
            }
            editor.apply()
        }
    }

    // Parental Gate for sensitive actions (Delete, Share)
    fun requestParentalGate(action: () -> Unit) {
        val a = Random.nextInt(2, 6)
        val b = Random.nextInt(2, 6)
        pendingParentalSuccess = action
        _uiState.update {
            it.copy(parentalGateChallenge = Pair("What is $a + $b?", a + b))
        }
    }

    fun answerParentalGate(answer: String): Boolean {
        val expected = _uiState.value.parentalGateChallenge?.second ?: return false
        val isCorrect = answer.trim().toIntOrNull() == expected
        if (isCorrect) {
            SoundPlayer.playSparkle()
            val action = pendingParentalSuccess
            _uiState.update { it.copy(parentalGateChallenge = null) }
            action?.invoke()
            pendingParentalSuccess = null
            return true
        } else {
            SoundPlayer.playWhoosh()
            return false
        }
    }

    fun dismissParentalGate() {
        _uiState.update { it.copy(parentalGateChallenge = null) }
        pendingParentalSuccess = null
    }
}
