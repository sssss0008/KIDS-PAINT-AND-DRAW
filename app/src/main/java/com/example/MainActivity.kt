package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.DrawingRepository
import com.example.ui.screens.ColoringBookScreen
import com.example.ui.screens.DrawingScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.DrawingViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = DrawingRepository(applicationContext)

        setContent {
            MyApplicationTheme {
                val viewModel: DrawingViewModel = viewModel {
                    DrawingViewModel(repository, applicationContext)
                }
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (state.currentScreen) {
                        AppScreen.CANVAS -> {
                            DrawingScreen(viewModel = viewModel, state = state)
                        }
                        AppScreen.COLORING_BOOK -> {
                            ColoringBookScreen(viewModel = viewModel)
                        }
                        AppScreen.GALLERY -> {
                            GalleryScreen(viewModel = viewModel, state = state)
                        }
                    }
                }
            }
        }
    }
}
