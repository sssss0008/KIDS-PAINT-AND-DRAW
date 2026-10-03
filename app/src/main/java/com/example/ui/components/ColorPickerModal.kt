package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val ExtendedKidColors = listOf(
    // Row 1: Brights
    Color(0xFFFF1744), Color(0xFFFF5252), Color(0xFFFF4081), Color(0xFFF50057),
    Color(0xFFE040FB), Color(0xFF7C4DFF), Color(0xFF536DFE), Color(0xFF448AFF),
    // Row 2: Greens & Teals
    Color(0xFF00E5FF), Color(0xFF1DE9B6), Color(0xFF00E676), Color(0xFF76FF03),
    Color(0xFFEEFF41), Color(0xFFFFEA00), Color(0xFFFFD600), Color(0xFFFF9100),
    // Row 3: Warm & Earthy
    Color(0xFFFF6D00), Color(0xFFFF3D00), Color(0xFF8D6E63), Color(0xFF5D4037),
    Color(0xFFCFD8DC), Color(0xFF90A4AE), Color(0xFF37474F), Color(0xFF000000),
    // Row 4: Pastels
    Color(0xFFFFCDD2), Color(0xFFF8BBD0), Color(0xFFE1BEE7), Color(0xFFD1C4E9),
    Color(0xFFBBDEFB), Color(0xFFB2EBF2), Color(0xFFC8E6C9), Color(0xFFFFF9C4)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerModal(
    initialColor: Color,
    onColorPicked: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedColor by remember { mutableStateOf(initialColor) }
    var hueSlider by remember { mutableFloatStateOf(0f) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pick Any Color! 🎨",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_color_modal")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Preview & Hue Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(selectedColor)
                        .border(3.dp, Color(0xFFCBD5E0), CircleShape)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Rainbow Slider 🌈",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF718096)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(
                                Brush.horizontalGradient(
                                    (0..360 step 30).map { Color.hsv(it.toFloat(), 1f, 1f) }
                                )
                            )
                    )
                    Slider(
                        value = hueSlider,
                        onValueChange = { hue ->
                            hueSlider = hue
                            selectedColor = Color.hsv(hue, 0.9f, 0.95f)
                        },
                        valueRange = 0f..360f,
                        colors = SliderDefaults.colors(
                            thumbColor = selectedColor,
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                }
            }

            // Quick Color Grid
            Text(
                text = "Favorite Colors",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color(0xFF4A5568)
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 42.dp),
                contentPadding = PaddingValues(2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                items(ExtendedKidColors) { color ->
                    val isSelected = selectedColor == color
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color(0xFF2D3748) else Color(0x22000000),
                                shape = CircleShape
                            )
                            .clickable { selectedColor = color }
                            .testTag("color_choice_${color.value}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            val iconColor = if (color.luminance() > 0.6f) Color.Black else Color.White
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Picked",
                                tint = iconColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Confirm Button
            Button(
                onClick = { onColorPicked(selectedColor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_color_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Use This Color! ✨", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
