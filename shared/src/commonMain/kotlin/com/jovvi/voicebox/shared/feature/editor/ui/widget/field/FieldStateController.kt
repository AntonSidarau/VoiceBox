package com.jovvi.voicebox.shared.feature.editor.ui.widget.field

import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.common.SimpleLogger
import com.jovvi.voicebox.shared.feature.editor.EditorHelpers
import com.jovvi.voicebox.shared.feature.editor.EditorSettings
import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator
import com.jovvi.voicebox.shared.feature.editor.helper.SharedStateHolder

private const val PIXELS_PER_SECOND = 200

class FieldStateController(
    private val sharedStateHolder: SharedStateHolder,
    private val simpleLogger: SimpleLogger
) {

    private lateinit var gridController: GridController
    private lateinit var loopsController: LoopsController

    var fieldHeight: Float = 0F
        private set
    var cellHeight: Float = 0F
        private set
    var bottomNotContentCellPosition = 0F
        private set

    private var cellMargin: Float = 0F
    private var cellWidth: Float = 0F
    private val preComputedLoopWidths: MutableMap<Loop.Size, Float> = mutableMapOf()

    val columns: Array<FieldColumn> get() = gridController.columns
    val loops: Collection<FieldLoop> get() = loopsController.loops
    val virtualStartPos: Float get() = gridController.virtualStartPos

    fun initialize(
        fieldWidth: Int,
        sizesCalculator: EditorSizesCalculator
    ) {
        fieldHeight = sizesCalculator.fieldHeight
        cellHeight = sizesCalculator.cellHeight
        cellMargin = sizesCalculator.cellMargin
        cellWidth = sizesCalculator.cellWidth
        bottomNotContentCellPosition = (cellHeight + cellMargin) *
                (EditorSettings.FIELD_CONTENT_CELLS_COUNT + 1)
        preComputeLoopWidths(cellWidth, cellMargin)

        sharedStateHolder.resetFieldState()
        gridController = GridController(fieldWidth, sizesCalculator, sharedStateHolder)
        loopsController = LoopsController(sizesCalculator, simpleLogger)
    }

    fun updateOnScroll(distanceX: Float): Boolean {
        return gridController.updateColumnsOnScroll(distanceX)
    }

    fun normalizeVelocity(velocity: Float, maxFlingVelocity: Int): Float {
        return velocity / maxFlingVelocity * PIXELS_PER_SECOND
    }

    fun getLoopWidth(loopSize: Loop.Size): Float {
        return preComputedLoopWidths.getValue(loopSize)
    }

    fun addDraggedLoop(xPos: Float, yPos: Float, loop: Loop) {
        val virtualStartPos = gridController.virtualStartPos
        val isOutFromVisibleBounds = xPos < 0
        val isNearStart = virtualStartPos < cellMargin && xPos < cellMargin
        val loopWidth = preComputedLoopWidths.getValue(loop.size)

        val xPosInternal = when {
            isOutFromVisibleBounds -> xPos + loopWidth + cellMargin
            isNearStart -> cellMargin
            else -> xPos
        }

        val columnIndex = gridController.calculateColumnIndex(xPosInternal)
        val rowIndex = gridController.calculateRowIndexWithRounding(yPos)

        val columnNumber = if (columnIndex == -1) -1 else columns[columnIndex].number
        val rowNumber = if (rowIndex <= -1 || rowIndex >= 5) -1 else rowIndex
        loopsController.addDraggedLoop(
            rowNumber, columnNumber, loop, loopWidth, isOutFromVisibleBounds
        )
    }

    private fun preComputeLoopWidths(cellWidth: Float, cellMargin: Float) {
        preComputedLoopWidths.clear()
        preComputedLoopWidths.put(
            Loop.Size.TWO,
            EditorHelpers.getLoopWidth(cellWidth, cellMargin, Loop.Size.TWO)
        )
        preComputedLoopWidths.put(
            Loop.Size.FOUR,
            EditorHelpers.getLoopWidth(cellWidth, cellMargin, Loop.Size.FOUR)
        )
    }
}
