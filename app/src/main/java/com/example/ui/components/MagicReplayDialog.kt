package com.example.ui.components

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.audio.SoundPlayer
import com.example.ui.theme.KidsBlue
import com.example.ui.theme.KidsGreen
import com.example.ui.theme.KidsOrange
import com.example.ui.theme.KidsYellow
import com.example.viewmodel.DrawingUiState
import kotlinx.coroutines.delay

@Composable
fun MagicReplayDialog(
    state: DrawingUiState,
    onDismiss: () -> Unit
) {
    val totalActions = state.actions.size
    var currentStep by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    var speedMultiplier by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(isPlaying, currentStep, speedMultiplier) {
        if (isPlaying && currentStep < totalActions) {
            val delayMs = (400L / speedMultiplier).toLong().coerceAtLeast(80L)
            delay(delayMs)
            currentStep++
            SoundPlayer.playPop()
            if (currentStep >= totalActions) {
                isPlaying = false
                SoundPlayer.playCelebration()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFFFFF9E6),
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Magic Movie Replay 🎬",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Step $currentStep of $totalActions strokes",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = KidsOrange
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_replay_dialog")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Canvas Area (renders actions up to currentStep)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(22.dp))
                        .background(state.paperTheme.bgColor)
                        .border(4.dp, Color(0xFFFFD54F), RoundedCornerShape(22.dp))
                ) {
                    KidsCanvas(
                        state = state,
                        onTouchDown = {},
                        onTouchMove = {},
                        onTouchUp = {},
                        replayActionCount = currentStep
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Scrubber Slider
                if (totalActions > 0) {
                    Slider(
                        value = currentStep.toFloat(),
                        onValueChange = {
                            currentStep = it.toInt()
                            if (currentStep < totalActions) isPlaying = false
                        },
                        valueRange = 0f..totalActions.toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }

                // Playback Controls Hub
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Restart button
                        IconButton(
                            onClick = {
                                currentStep = 0
                                isPlaying = true
                                SoundPlayer.playWhoosh()
                            },
                            modifier = Modifier.testTag("replay_restart_button")
                        ) {
                            Icon(imageVector = Icons.Default.Replay, contentDescription = "Restart")
                        }

                        // Step backward
                        IconButton(
                            onClick = {
                                if (currentStep > 0) {
                                    currentStep--
                                    isPlaying = false
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Previous")
                        }

                        // Big Play / Pause button
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .shadow(3.dp, CircleShape)
                                .clip(CircleShape)
                                .background(KidsOrange)
                                .clickable {
                                    if (currentStep >= totalActions) currentStep = 0
                                    isPlaying = !isPlaying
                                    SoundPlayer.playPop()
                                }
                                .testTag("replay_play_pause_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Step forward
                        IconButton(
                            onClick = {
                                if (currentStep < totalActions) {
                                    currentStep++
                                    isPlaying = false
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next")
                        }

                        // Speed selector chip
                        FilterChip(
                            selected = speedMultiplier > 1f,
                            onClick = {
                                speedMultiplier = when (speedMultiplier) {
                                    1f -> 2f
                                    2f -> 4f
                                    else -> 1f
                                }
                                SoundPlayer.playPop()
                            },
                            label = { Text("${speedMultiplier.toInt()}x Speed") },
                            modifier = Modifier.testTag("replay_speed_button")
                        )
                    }
                }
            }
        }
    }
}
