package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DrawingRepository
import com.example.model.BrushType
import com.example.model.CanvasPaperTheme
import com.example.model.DrawAction
import com.example.model.PatternStyle
import com.example.model.ShapeType
import com.example.viewmodel.AppScreen
import com.example.viewmodel.DrawingViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DrawingViewModelTest {

    private lateinit var viewModel: DrawingViewModel

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = DrawingRepository(context)
        viewModel = DrawingViewModel(repository, context)
    }

    @Test
    fun testInitialState() {
        val state = viewModel.uiState.value
        assertEquals(AppScreen.CANVAS, state.currentScreen)
        assertEquals(BrushType.CRAYON, state.selectedBrush)
        assertEquals(0, state.actions.size)
        assertFalse(state.isSoundMuted)
    }

    @Test
    fun testNavigation() {
        viewModel.navigateTo(AppScreen.COLORING_BOOK)
        assertEquals(AppScreen.COLORING_BOOK, viewModel.uiState.value.currentScreen)

        viewModel.navigateTo(AppScreen.GALLERY)
        assertEquals(AppScreen.GALLERY, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun testDrawActionUndoRedo() {
        viewModel.onTouchDown(Offset(10f, 10f))
        viewModel.onTouchMove(Offset(20f, 20f))
        viewModel.onTouchUp()

        assertEquals(1, viewModel.uiState.value.actions.size)
        assertEquals(0, viewModel.uiState.value.redoStack.size)

        // Undo
        viewModel.undo()
        assertEquals(0, viewModel.uiState.value.actions.size)
        assertEquals(1, viewModel.uiState.value.redoStack.size)

        // Redo
        viewModel.redo()
        assertEquals(1, viewModel.uiState.value.actions.size)
        assertEquals(0, viewModel.uiState.value.redoStack.size)
    }

    @Test
    fun testClearCanvas() {
        viewModel.onTouchDown(Offset(10f, 10f))
        viewModel.onTouchUp()
        assertEquals(1, viewModel.uiState.value.actions.size)

        viewModel.clearCanvas()
        assertEquals(0, viewModel.uiState.value.actions.size)
        assertEquals(0, viewModel.uiState.value.redoStack.size)
    }

    @Test
    fun testAdvancedBrushesAndPatterns() {
        viewModel.setBrush(BrushType.BUBBLES)
        assertEquals(BrushType.BUBBLES, viewModel.uiState.value.selectedBrush)

        viewModel.setBrush(BrushType.FIREWORKS)
        assertEquals(BrushType.FIREWORKS, viewModel.uiState.value.selectedBrush)

        viewModel.setPattern(PatternStyle.PAW_PRINTS)
        assertEquals(PatternStyle.PAW_PRINTS, viewModel.uiState.value.selectedPattern)
        assertEquals(BrushType.PATTERN_ROLLER, viewModel.uiState.value.selectedBrush)
    }

    @Test
    fun testPaperThemeSelection() {
        viewModel.setPaperTheme(CanvasPaperTheme.CHALKBOARD)
        assertEquals(CanvasPaperTheme.CHALKBOARD, viewModel.uiState.value.paperTheme)
        assertEquals(CanvasPaperTheme.CHALKBOARD.defaultStrokeColor, viewModel.uiState.value.selectedColor)
    }

    @Test
    fun testFloodFillAction() {
        viewModel.setBrush(BrushType.BUCKET)
        viewModel.onTouchDown(Offset(100f, 100f))

        assertEquals(1, viewModel.uiState.value.actions.size)
        assertTrue(viewModel.uiState.value.actions.first() is DrawAction.FloodFillAction)
    }

    @Test
    fun testDailyPromptAcceptance() {
        viewModel.acceptDailyPrompt("Draw a dancing puppy in the rain! ☔🐶")
        assertEquals("Draw a dancing puppy in the rain! ☔🐶", viewModel.uiState.value.activeTemplateTitle)
        assertFalse(viewModel.uiState.value.showPromptDialog)
    }

    @Test
    fun testStampSelection() {
        viewModel.setStamp("🦄")
        assertEquals(BrushType.STAMP, viewModel.uiState.value.selectedBrush)
        assertEquals("🦄", viewModel.uiState.value.selectedStamp)

        viewModel.onTouchDown(Offset(50f, 50f))
        assertEquals(1, viewModel.uiState.value.actions.size)
    }

    @Test
    fun testColoringTemplateSelection() {
        viewModel.selectColoringTemplate("kitty", "Smiling Kitty")
        assertEquals("kitty", viewModel.uiState.value.activeTemplateId)
        assertEquals("Smiling Kitty", viewModel.uiState.value.activeTemplateTitle)
        assertEquals(AppScreen.CANVAS, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun testParentalGate() {
        var actionExecuted = false
        viewModel.requestParentalGate {
            actionExecuted = true
        }

        val challenge = viewModel.uiState.value.parentalGateChallenge
        assertNotNull(challenge)

        val failed = viewModel.answerParentalGate("999")
        assertFalse(failed)
        assertFalse(actionExecuted)

        val correctAnswer = challenge!!.second.toString()
        val success = viewModel.answerParentalGate(correctAnswer)
        assertTrue(success)
        assertTrue(actionExecuted)
        assertNull(viewModel.uiState.value.parentalGateChallenge)
    }
}
