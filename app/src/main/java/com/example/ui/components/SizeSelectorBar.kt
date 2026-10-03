package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SizeOption(
    val strokeWidth: Float,
    val animalEmoji: String,
    val label: String
)

val AnimalSizes = listOf(
    SizeOption(8f, "🐭", "Tiny"),
    SizeOption(20f, "🐰", "Medium"),
    SizeOption(38f, "🐻", "Big"),
    SizeOption(65f, "🐘", "Giant")
)

@Composable
fun SizeSelectorBar(
    currentStrokeWidth: Float,
    selectedColor: Color,
    onSelectWidth: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimalSizes.forEach { option ->
            val isSelected = kotlin.math.abs(currentStrokeWidth - option.strokeWidth) < 5f
            val scale by animateFloatAsState(targetValue = if (isSelected) 1.12f else 1.0f, label = "sizeScale")

            Box(
                modifier = Modifier
                    .scale(scale)
                    .shadow(if (isSelected) 6.dp else 2.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.White
                    )
                    .border(
                        width = if (isSelected) 2.5.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelectWidth(option.strokeWidth) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("size_option_${option.label.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = option.animalEmoji, fontSize = 22.sp)

                    // Stroke preview circle
                    val dotSize = (option.strokeWidth * 0.45f).coerceIn(6f, 26f)
                    Box(
                        modifier = Modifier
                            .size(dotSize.dp)
                            .clip(CircleShape)
                            .background(selectedColor)
                    )

                    Text(
                        text = option.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color(0xFF718096)
                    )
                }
            }
        }
    }
}
