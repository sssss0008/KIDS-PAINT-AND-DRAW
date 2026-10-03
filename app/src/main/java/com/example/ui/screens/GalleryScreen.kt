package com.example.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.SavedDrawing
import com.example.ui.components.BadgeCabinetDialog
import com.example.ui.components.MagicReplayDialog
import com.example.ui.components.ParentalGateDialog
import com.example.ui.theme.KidsBlue
import com.example.ui.theme.KidsOrange
import com.example.ui.theme.KidsPurple
import com.example.ui.theme.KidsRed
import com.example.ui.theme.KidsYellow
import com.example.viewmodel.AppScreen
import com.example.viewmodel.DrawingUiState
import com.example.viewmodel.DrawingViewModel
import java.io.File

@Composable
fun GalleryScreen(
    viewModel: DrawingViewModel,
    state: DrawingUiState
) {
    val context = LocalContext.current

    BackHandler {
        if (state.fullscreenDrawing != null) {
            viewModel.openFullscreenDrawing(null)
        } else {
            viewModel.navigateTo(AppScreen.CANVAS)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9E6))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.CANVAS) },
                    modifier = Modifier.testTag("gallery_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Canvas",
                        tint = Color(0xFF2D3748)
                    )
                }

                Text(
                    text = "My Art Gallery 🖼️",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Trophy button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF3C7))
                            .clickable { viewModel.setPickerSheet(badges = true) }
                            .testTag("gallery_trophy_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏆", fontSize = 18.sp)
                    }

                    // Draw button
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.CANVAS) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KidsOrange),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("gallery_new_drawing_button")
                    ) {
                        Text("Draw! 🎨", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Empty State or Grid
        if (state.savedDrawings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "🎨✨", fontSize = 64.sp)
                    Text(
                        text = "Your Gallery is Empty!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                    Text(
                        text = "Draw something magical with crayons, bubbles, neon, and stamps, then tap the star ⭐ to save it here!",
                        fontSize = 15.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = Color(0xFF718096)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.CANVAS) },
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KidsOrange),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("empty_gallery_draw_button")
                    ) {
                        Text("Create My First Artwork! 🖌️", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.savedDrawings, key = { it.id }) { drawing ->
                    GalleryDrawingCard(
                        drawing = drawing,
                        onClick = { viewModel.openFullscreenDrawing(drawing) }
                    )
                }
            }
        }
    }

    // Fullscreen Drawing Viewer Dialog
    state.fullscreenDrawing?.let { drawing ->
        FullscreenDrawingDialog(
            drawing = drawing,
            onDismiss = { viewModel.openFullscreenDrawing(null) },
            onShare = {
                viewModel.requestParentalGate {
                    val shareIntent = viewModel.getShareIntent(drawing)
                    if (shareIntent != null) {
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Drawing With"))
                    }
                }
            },
            onDelete = {
                viewModel.requestParentalGate {
                    viewModel.deleteDrawing(drawing.id)
                }
            }
        )
    }

    if (state.showBadgesDialog) {
        BadgeCabinetDialog(
            badges = state.badges,
            onDismiss = { viewModel.setPickerSheet(badges = false) }
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

@Composable
private fun GalleryDrawingCard(
    drawing: SavedDrawing,
    onClick: () -> Unit
) {
    val bitmap = remember(drawing.filePath) {
        try {
            if (File(drawing.filePath).exists()) {
                BitmapFactory.decodeFile(drawing.filePath)
            } else null
        } catch (_: Exception) {
            null
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(5.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .border(3.dp, Color(0xFFFFD54F), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag("gallery_item_${drawing.id}"),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFFDF5))
                    .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = drawing.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(text = "🎨", fontSize = 32.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = drawing.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF2D3748),
                maxLines = 1
            )

            Text(
                text = drawing.dateText,
                fontSize = 11.sp,
                color = Color(0xFF718096)
            )
        }
    }
}

@Composable
private fun FullscreenDrawingDialog(
    drawing: SavedDrawing,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val bitmap = remember(drawing.filePath) {
        try {
            if (File(drawing.filePath).exists()) {
                BitmapFactory.decodeFile(drawing.filePath)
            } else null
        } catch (_: Exception) {
            null
        }
    }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFFFFFDF5),
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = drawing.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3748)
                        )
                        Text(
                            text = drawing.dateText,
                            fontSize = 12.sp,
                            color = Color(0xFF718096)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_fullscreen_dialog")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(4.dp, Color(0xFFFFD54F), RoundedCornerShape(20.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = drawing.title,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("Unable to load picture 🎨", color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Delete Button
                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = KidsRed),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("delete_artwork_button")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", fontWeight = FontWeight.Bold)
                    }

                    // Share Button
                    Button(
                        onClick = onShare,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KidsBlue),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(52.dp)
                            .testTag("share_artwork_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share 💌", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                shape = RoundedCornerShape(24.dp),
                title = { Text("Delete Masterpiece?") },
                text = { Text("Are you sure you want to remove this drawing from your gallery?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteConfirm = false
                            onDelete()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KidsRed),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Yes, Delete", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDeleteConfirm = false },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Keep It")
                    }
                }
            )
        }
    }
}
