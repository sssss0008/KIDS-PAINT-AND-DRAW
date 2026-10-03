package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ColoringPage
import com.example.model.ColoringTemplates
import com.example.ui.theme.KidsBlue
import com.example.ui.theme.KidsGreen
import com.example.ui.theme.KidsOrange
import com.example.ui.theme.KidsPurple
import com.example.ui.theme.KidsYellow
import com.example.viewmodel.AppScreen
import com.example.viewmodel.DrawingViewModel

@Composable
fun ColoringBookScreen(
    viewModel: DrawingViewModel
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.CANVAS)
    }

    val templates = ColoringTemplates.allTemplates

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9E6))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Header
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
                    modifier = Modifier.testTag("coloring_book_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Canvas",
                        tint = Color(0xFF2D3748)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Coloring Pages 📚",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                }

                // Blank Canvas option
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE8F5E9))
                        .clickable { viewModel.selectColoringTemplate(null, null) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("select_blank_canvas_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Blank 📄",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = KidsGreen
                    )
                }
            }
        }

        // Subtitle prompt
        Text(
            text = "Pick a picture to color with magic brushes! ✨",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF718096),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        // Cards Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(templates) { template ->
                ColoringPageCard(
                    template = template,
                    onSelect = {
                        viewModel.selectColoringTemplate(template.id, template.title)
                    }
                )
            }
        }
    }
}

@Composable
private fun ColoringPageCard(
    template: ColoringPage,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .border(2.5.dp, Color(0xFFFFE082), RoundedCornerShape(20.dp))
            .clickable { onSelect() }
            .testTag("coloring_card_${template.id}"),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live vector thumbnail preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFFDF5))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    ColoringTemplates.drawTemplate(this, template.id)
                }

                // Category tag badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.9f),
                    shadowElevation = 1.dp
                ) {
                    Text(
                        text = template.emoji,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = template.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )

            Text(
                text = template.description,
                fontSize = 12.sp,
                color = Color(0xFF718096),
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSelect,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KidsOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("color_page_button_${template.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Brush,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text("Color It! 🎨", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
