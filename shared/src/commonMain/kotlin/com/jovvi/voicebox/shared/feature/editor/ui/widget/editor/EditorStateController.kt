package com.jovvi.voicebox.shared.feature.editor.ui.widget.editor

import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator
import com.jovvi.voicebox.shared.feature.editor.helper.SharedStateHolder

private const val DRAG_OFFSET_FACTOR = 0.33F

class EditorStateController(
    private val sizeCalculator: EditorSizesCalculator,
    private val sharedStateHolder: SharedStateHolder
) {

    private lateinit var state: EditorState

    private var totalWidth: Float = 0F
    private var totalHeight: Float = 0F
    private var fieldTotalWidth: Float = 0F
    private var fieldCellWidth: Float = 0F
    private var fieldCellHeight: Float = 0F

    val draggingLoop: Loop? get() = state.draggingLoop
    val draggingXPos: Float get() = state.draggingLoopXPos
    val draggingYPos: Float get() = state.draggingLoopYPos

    fun initialize(width: Float, height: Float) {
        totalWidth = width
        totalHeight = height
        fieldTotalWidth = sizeCalculator.totalFieldWidth
        fieldCellWidth = sizeCalculator.cellWidth
        fieldCellHeight = sizeCalculator.cellHeight

        state = EditorState(draggingLoopXPos = -1F, draggingLoopYPos = -1F)
    }

    fun startDragging(tapXPos: Float, tapYPos: Float, loop: Loop) {
        val cellMargin = sizeCalculator.cellMargin

        state.updateDraggingLoop(loop, fieldCellWidth, cellMargin)
        initDraggingCoordinates(tapXPos, tapYPos)
    }

    fun updateDraggingPositions(deltaX: Float, deltaY: Float) {
        state.draggingLoop ?: return

        updateDraggingXPos(deltaX)
        updateDraggingYPos(deltaY)
    }

    private fun updateDraggingXPos(deltaX: Float) {
        val threshold = fieldTotalWidth - state.loopWidth
        val fieldVirtualStartPos = sharedStateHolder.fieldVirtualStartPos
        val currentPos = state.draggingLoopXPos
        val x = currentPos - deltaX
        val newPosition = x + fieldVirtualStartPos

        state.draggingLoopXPos = when {
            x < 0 && fieldVirtualStartPos <= 0F -> 0F
            newPosition < 0 -> state.draggingLoopXPos
            newPosition >= threshold -> state.draggingLoopXPos
            else -> x
        }
    }

    private fun updateDraggingYPos(deltaY: Float) {
        val threshold = totalHeight - fieldCellHeight
        val currentPos = state.draggingLoopYPos
        val y = currentPos - deltaY

        state.draggingLoopYPos = when {
            y > threshold -> threshold
            y < 0F -> 0F
            else -> y
        }
    }

    fun stopDragging() {
        state.removeDraggingLoop()
        state.draggingLoopXPos = -1F
        state.draggingLoopYPos = -1F
    }

    fun isDraggingLoopInBounds(
        xLeft: Float,
        yTop: Float,
        xRight: Float,
        yBottom: Float
    ): Boolean {
        if (draggingLoop == null) return false

        val isInVerticalBounds = state.draggingLoopYPos in yTop..yBottom
        val isInHorizontalBounds = state.draggingLoopXPos in xLeft..xRight

        return isInHorizontalBounds && isInVerticalBounds
    }

    private fun initDraggingCoordinates(x: Float, y: Float) {
        val offset = sizeCalculator.cellHeight * DRAG_OFFSET_FACTOR
        val xPos = x + offset
        val yPos = y - offset
        val thresholdX = fieldTotalWidth - state.loopWidth
        val thresholdY = totalHeight
        val fieldVirtualStartPos = sharedStateHolder.fieldVirtualStartPos
        val newXPosition = xPos + fieldVirtualStartPos

        state.draggingLoopXPos = when {
            xPos < 0 && fieldVirtualStartPos <= 0F -> 0F
            newXPosition >= thresholdX -> xPos - (newXPosition - thresholdX)
            else -> xPos
        }

        state.draggingLoopYPos = when {
            yPos > thresholdY -> thresholdY
            yPos < 0F -> 0F
            else -> yPos
        }
    }
}
