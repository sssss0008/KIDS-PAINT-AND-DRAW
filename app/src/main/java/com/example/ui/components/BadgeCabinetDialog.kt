package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KidBadge
import com.example.ui.theme.KidsGreen
import com.example.ui.theme.KidsOrange
import com.example.ui.theme.KidsYellow

@Composable
fun BadgeCabinetDialog(
    badges: List<KidBadge>,
    onDismiss: () -> Unit
) {
    val unlockedCount = badges.count { it.isUnlocked }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Trophy Cabinet 🏆",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF2D3748)
                    )
                    Text(
                        text = "$unlockedCount of ${badges.size} Badges Earned! ⭐",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KidsOrange
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            ) {
                items(badges, key = { it.id }) { badge ->
                    BadgeCard(badge = badge)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("close_trophy_dialog")
            ) {
                Text("Keep Drawing! 🎨", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {}
    )
}

@Composable
private fun BadgeCard(badge: KidBadge) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (badge.isUnlocked) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(
            width = if (badge.isUnlocked) 2.dp else 1.dp,
            color = if (badge.isUnlocked) Color(0xFFFCD34D) else Color(0xFFE2E8F0)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(if (badge.isUnlocked) Color(0xFFFEF3C7) else Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (badge.isUnlocked) badge.iconEmoji else "🔒",
                    fontSize = 24.sp
                )
            }

            Text(
                text = badge.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (badge.isUnlocked) Color(0xFF92400E) else Color(0xFF64748B),
                maxLines = 1
            )

            Text(
                text = badge.description,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF94A3B8),
                lineHeight = 13.sp,
                maxLines = 2
            )

            if (!badge.isUnlocked) {
                val progressFrac = (badge.progress.toFloat() / badge.target.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progressFrac },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = KidsOrange,
                    trackColor = Color(0xFFCBD5E1)
                )
                Text(
                    text = "${badge.progress}/${badge.target}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B)
                )
            } else {
                Text(
                    text = "UNLOCKED! ✨",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = KidsGreen
                )
            }
        }
    }
}
