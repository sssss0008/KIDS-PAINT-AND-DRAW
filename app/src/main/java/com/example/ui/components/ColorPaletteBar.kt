package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DrawingColors

@Composable
fun ColorPaletteBar(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    onOpenColorPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color Picker opener button
        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(3.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(
                            Color(0xFFFF1744),
                            Color(0xFFFFD600),
                            Color(0xFF00E676),
                            Color(0xFF00B0FF),
                            Color(0xFF651FFF),
                            Color(0xFFFF1744)
                        )
                    )
                )
                .border(2.dp, Color.White, CircleShape)
                .clickable { onOpenColorPicker() }
                .testTag("open_color_picker_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = "More Colors",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Color circles
        DrawingColors.forEachIndexed { index, color ->
            val isSelected = selectedColor == color
            val scale by animateFloatAsState(targetValue = if (isSelected) 1.15f else 1.0f, label = "colorScale")

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .scale(scale)
                    .shadow(if (isSelected) 5.dp else 2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (isSelected) 3.5.dp else 1.5.dp,
                        color = if (isSelected) Color.White else Color(0x33000000),
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(color) }
                    .testTag("color_item_$index"),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    val checkColor = if (color.luminance() > 0.6f) Color(0xFF2D3748) else Color.White
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = checkColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
