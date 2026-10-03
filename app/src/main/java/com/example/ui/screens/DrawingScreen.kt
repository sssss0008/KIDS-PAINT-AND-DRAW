package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrushType
import com.example.model.SymmetryMode
import com.example.ui.components.BadgeCabinetDialog
import com.example.ui.components.BrushSelector
import com.example.ui.components.ClearConfirmDialog
import com.example.ui.components.ColorPaletteBar
import com.example.ui.components.ColorPickerModal
import com.example.ui.components.ConfettiOverlay
import com.example.ui.components.DrawingPromptDialog
import com.example.ui.components.KidsCanvas
import com.example.ui.components.MagicReplayDialog
import com.example.ui.components.PaperThemePicker
import com.example.ui.components.ParentalGateDialog
import com.example.ui.components.PatternPickerSheet
import com.example.ui.components.ShapePickerSheet
import com.example.ui.components.SizeSelectorBar
import com.example.ui.components.StampPickerSheet
import com.example.ui.components.exportDrawingToBitmap
import com.example.ui.theme.KidsGreen
import com.example.ui.theme.KidsOrange
import com.example.ui.theme.KidsPurple
import com.example.ui.theme.KidsRed
import com.example.ui.theme.KidsYellow
import com.example.viewmodel.AppScreen
import com.example.viewmodel.DrawingUiState
import com.example.viewmodel.DrawingViewModel

@Composable
fun DrawingScreen(
    viewModel: DrawingViewModel,
    state: DrawingUiState
) {
    var canvasW by remember { mutableIntStateOf(1080) }
    var canvasH by remember { mutableIntStateOf(1440) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9E6))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Toolbar
            TopKidBar(
                state = state,
                onNavigateColoring = { viewModel.navigateTo(AppScreen.COLORING_BOOK) },
                onNavigateGallery = { viewModel.navigateTo(AppScreen.GALLERY) },
                onOpenBadges = { viewModel.setPickerSheet(badges = true) },
                onOpenPrompt = { viewModel.setPickerSheet(prompt = true) },
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() },
                onClear = { viewModel.showClearConfirm(true) },
                onReplay = { viewModel.setPickerSheet(replay = true) },
                onOpenPaperTheme = { viewModel.setPickerSheet(paper = true) },
                onToggleSymmetry = { viewModel.cycleSymmetry() },
                onToggleSound = { viewModel.toggleSound() },
                onSave = {
                    val bitmap = exportDrawingToBitmap(state, canvasW, canvasH)
                    viewModel.saveArtwork(bitmap, state.activeTemplateTitle)
                }
            )

            // Drawing Canvas Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp, vertical = 2.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .border(4.dp, Color(0xFFFFD54F), RoundedCornerShape(24.dp))
            ) {
                KidsCanvas(
                    state = state,
                    onTouchDown = { viewModel.onTouchDown(it) },
                    onTouchMove = { viewModel.onTouchMove(it) },
                    onTouchUp = { viewModel.onTouchUp() },
                    onCanvasSizeChanged = { w, h ->
                        canvasW = w
                        canvasH = h
                    }
                )

                // Active prompt/template badge
                state.activeTemplateTitle?.let { title ->
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.92f),
                        shadowElevation = 3.dp
                    ) {
                        Text(
                            text = "🎨 $title",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Active Symmetry Indicator
                if (state.symmetryMode != SymmetryMode.OFF) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = KidsPurple.copy(alpha = 0.92f),
                        shadowElevation = 3.dp
                    ) {
                        Text(
                            text = state.symmetryMode.iconEmoji + " " + state.symmetryMode.displayName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Bottom Tools Control Hub
            BottomToolsHub(
                state = state,
                viewModel = viewModel
            )
        }

        // Confetti Celebration
        if (state.showConfetti) {
            ConfettiOverlay()
        }

        // Saving Modal Overlay
        if (state.isSaving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = KidsOrange, modifier = Modifier.size(32.dp))
                        Text(
                            text = "Saving Masterpiece... ⭐",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Dialogs & Sheets
        if (state.showClearDialog) {
            ClearConfirmDialog(
                onConfirm = { viewModel.clearCanvas() },
                onDismiss = { viewModel.showClearConfirm(false) }
            )
        }

        if (state.showColorPickerSheet) {
            ColorPickerModal(
                initialColor = state.selectedColor,
                onColorPicked = { viewModel.setColor(it) },
                onDismiss = { viewModel.setPickerSheet(color = false) }
            )
        }

        if (state.showStampSheet) {
            StampPickerSheet(
                onStampSelected = { viewModel.setStamp(it) },
                onDismiss = { viewModel.setPickerSheet(stamp = false) }
            )
        }

        if (state.showShapeSheet) {
            ShapePickerSheet(
                currentShape = state.selectedShape,
                isFilled = state.isShapeFilled,
                onShapeSelected = { shape, filled -> viewModel.setShape(shape, filled) },
                onDismiss = { viewModel.setPickerSheet(shape = false) }
            )
        }

        if (state.showPaperThemeSheet) {
            PaperThemePicker(
                currentTheme = state.paperTheme,
                onThemeSelected = { viewModel.setPaperTheme(it) },
                onDismiss = { viewModel.setPickerSheet(paper = false) }
            )
        }

        if (state.showPatternSheet) {
            PatternPickerSheet(
                currentPattern = state.selectedPattern,
                onPatternSelected = { viewModel.setPattern(it) },
                onDismiss = { viewModel.setPickerSheet(pattern = false) }
            )
        }

        if (state.showPromptDialog) {
            DrawingPromptDialog(
                onAcceptPrompt = { viewModel.acceptDailyPrompt(it) },
                onDismiss = { viewModel.setPickerSheet(prompt = false) }
            )
        }

        if (state.showBadgesDialog) {
            BadgeCabinetDialog(
                badges = state.badges,
                onDismiss = { viewModel.setPickerSheet(badges = false) }
            )
        }

        if (state.showReplayDialog) {
            MagicReplayDialog(
                state = state,
                onDismiss = { viewModel.setPickerSheet(replay = false) }
            )
        }

        state.parentalGateChallenge?.let { challenge ->
            ParentalGateDialog(
                question = challenge.first,
                onAnswerSubmitted = { viewModel.answerParentalGate(it) },
                onDismiss = { viewModel.dismissParentalGate() }
            )
        }
    }
}

@Composable
private fun TopKidBar(
    state: DrawingUiState,
    onNavigateColoring: () -> Unit,
    onNavigateGallery: () -> Unit,
    onOpenBadges: () -> Unit,
    onOpenPrompt: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onClear: () -> Unit,
    onReplay: () -> Unit,
    onOpenPaperTheme: () -> Unit,
    onToggleSymmetry: () -> Unit,
    onToggleSound: () -> Unit,
    onSave: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 3.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Group 1: Navigation & Quests
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                KidCircleButton(
                    emoji = "📚",
                    onClick = onNavigateColoring,
                    bgColor = Color(0xFFE8F5E9),
                    tag = "nav_coloring_book_button",
                    contentDescription = "Coloring Book"
                )
                KidCircleButton(
                    emoji = "🖼️",
                    onClick = onNavigateGallery,
                    bgColor = Color(0xFFE3F2FD),
                    tag = "nav_gallery_button",
                    contentDescription = "My Gallery"
                )
                KidCircleButton(
                    emoji = "🏆",
                    onClick = onOpenBadges,
                    bgColor = Color(0xFFFEF3C7),
                    tag = "nav_trophies_button",
                    contentDescription = "Trophies & Badges"
                )
                KidCircleButton(
                    emoji = "🎲",
                    onClick = onOpenPrompt,
                    bgColor = Color(0xFFF3E8FF),
                    tag = "idea_spinner_button",
                    contentDescription = "Idea Quest"
                )
            }

            // Group 2: Canvas Actions
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                KidIconButton(
                    icon = Icons.AutoMirrored.Filled.Undo,
                    enabled = state.actions.isNotEmpty(),
                    onClick = onUndo,
                    tag = "undo_button",
                    contentDescription = "Undo"
                )
                KidIconButton(
                    icon = Icons.AutoMirrored.Filled.Redo,
                    enabled = state.redoStack.isNotEmpty(),
                    onClick = onRedo,
                    tag = "redo_button",
                    contentDescription = "Redo"
                )
                KidCircleButton(
                    emoji = "🧽",
                    onClick = onClear,
                    bgColor = Color(0xFFFFEBEE),
                    tag = "clear_canvas_button",
                    contentDescription = "Clean Canvas"
                )
                KidCircleButton(
                    emoji = "🎬",
                    onClick = onReplay,
                    bgColor = Color(0xFFE0F2FE),
                    tag = "magic_replay_button",
                    contentDescription = "Magic Replay"
                )
            }

            // Group 3: Paper, Symmetry, Sound, Save
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                KidCircleButton(
                    emoji = state.paperTheme.iconEmoji,
                    onClick = onOpenPaperTheme,
                    bgColor = Color(0xFFF1F5F9),
                    tag = "paper_theme_button",
                    contentDescription = "Paper Theme"
                )
                KidCircleButton(
                    emoji = state.symmetryMode.iconEmoji,
                    onClick = onToggleSymmetry,
                    bgColor = if (state.symmetryMode != SymmetryMode.OFF) Color(0xFFEDE7F6) else Color(0xFFF5F5F5),
                    tag = "symmetry_button",
                    contentDescription = "Symmetry"
                )
                KidCircleButton(
                    emoji = if (state.isSoundMuted) "🔇" else "🔊",
                    onClick = onToggleSound,
                    bgColor = Color(0xFFFFF8E1),
                    tag = "sound_toggle_button",
                    contentDescription = "Sound Toggle"
                )
                KidCircleButton(
                    emoji = "⭐",
                    onClick = onSave,
                    bgColor = KidsOrange,
                    tintWhite = true,
                    tag = "save_drawing_button",
                    contentDescription = "Save Drawing"
                )
            }
        }
    }
}

@Composable
private fun BottomToolsHub(
    state: DrawingUiState,
    viewModel: DrawingViewModel
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 2.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Row 1: Advanced Brushes Selector
            BrushSelector(
                selectedBrush = state.selectedBrush,
                onSelectBrush = { brush ->
                    if (brush == BrushType.STAMP) {
                        viewModel.setPickerSheet(stamp = true)
                    } else if (brush == BrushType.PATTERN_ROLLER) {
                        viewModel.setPickerSheet(pattern = true)
                    } else {
                        viewModel.setBrush(brush)
                    }
                }
            )

            // Row 2: Secondary Tool Pill Buttons & Quick Colors
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary tool pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    KidPillButton(
                        text = "Stickers ${state.selectedStamp}",
                        onClick = { viewModel.setPickerSheet(stamp = true) },
                        bgColor = Color(0xFFFFF3E0),
                        tag = "open_stamps_hub"
                    )
                    KidPillButton(
                        text = "Shapes 📐",
                        onClick = { viewModel.setPickerSheet(shape = true) },
                        bgColor = Color(0xFFEDE7F6),
                        tag = "open_shapes_hub"
                    )
                    if (state.selectedBrush == BrushType.PATTERN_ROLLER) {
                        KidPillButton(
                            text = "Roller ${state.selectedPattern.emoji}",
                            onClick = { viewModel.setPickerSheet(pattern = true) },
                            bgColor = Color(0xFFFCE7F3),
                            tag = "open_patterns_hub"
                        )
                    }
                }

                // Quick Color bar
                ColorPaletteBar(
                    selectedColor = state.selectedColor,
                    onColorSelected = { viewModel.setColor(it) },
                    onOpenColorPicker = { viewModel.setPickerSheet(color = true) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 3: Animal Size Selector
            SizeSelectorBar(
                currentStrokeWidth = state.strokeWidth,
                selectedColor = state.selectedColor,
                onSelectWidth = { viewModel.setStrokeWidth(it) }
            )
        }
    }
}

@Composable
private fun KidCircleButton(
    emoji: String,
    onClick: () -> Unit,
    bgColor: Color,
    tag: String,
    contentDescription: String,
    tintWhite: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .shadow(2.dp, CircleShape)
            .clip(CircleShape)
            .background(bgColor)
            .clickable { onClick() }
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun KidIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
    tag: String,
    contentDescription: String
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (enabled) Color(0xFFF1F5F9) else Color(0xFFF8FAFC))
            .clickable(enabled = enabled) { onClick() }
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) Color(0xFF334155) else Color(0xFFCBD5E1),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun KidPillButton(
    text: String,
    onClick: () -> Unit,
    bgColor: Color,
    tag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
    }
}
