package com.jovvi.voicebox.shared.feature.editor.ui.widget.field

import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.feature.editor.EditorSettings
import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator
import com.jovvi.voicebox.shared.feature.editor.helper.SharedStateHolder

private const val PIXELS_PER_SECOND = 200

class FieldStateController(
    private val sharedStateHolder: SharedStateHolder,
    private val sizesCalculator: EditorSizesCalculator
) {

    private lateinit var gridController: GridController
    private lateinit var loopsController: LoopsController

    private var cellMargin: Float = 0F
    private var cellWidth: Float = 0F

    var fieldHeight: Float = 0F
        private set
    var cellHeight: Float = 0F
        private set
    var bottomNotContentCellPosition = 0F
        private set

    val columns: Array<FieldColumn> get() = gridController.columns
    val loops: Collection<FieldLoop> get() = loopsController.loops
    val virtualStartPos: Float get() = gridController.virtualStartPos

    fun initialize(fieldWidth: Int) {
        sizesCalculator.ensureInitialized()

        fieldHeight = sizesCalculator.fieldHeight
        cellHeight = sizesCalculator.cellHeight
        cellMargin = sizesCalculator.cellMargin
        cellWidth = sizesCalculator.cellWidth
        bottomNotContentCellPosition = (cellHeight + cellMargin) *
                (EditorSettings.FIELD_CONTENT_CELLS_COUNT + 1)

        sharedStateHolder.resetFieldState()
        gridController = GridController(fieldWidth, sizesCalculator, sharedStateHolder)
        loopsController = LoopsController(sizesCalculator)
    }

    fun updateOnScroll(distanceX: Float): Boolean {
        return gridController.updateColumnsOnScroll(distanceX)
    }

    fun normalizeVelocity(velocity: Float, maxFlingVelocity: Int): Float {
        return velocity / maxFlingVelocity * PIXELS_PER_SECOND
    }

    fun isInContentBounds(yPos: Float): Boolean {
        return yPos in cellHeight..fieldHeight - cellHeight
    }

    fun addDraggedLoop(xPos: Float, yPos: Float, loop: Loop) {
        val virtualStartPos = gridController.virtualStartPos
        val isOutFromVisibleBounds = xPos < 0
        val isNearStart = virtualStartPos < cellMargin && xPos < cellMargin
        val loopWidth = sizesCalculator.getLoopWidth(loop.size)

        val xPosInternal = when {
            isOutFromVisibleBounds -> xPos + loopWidth + cellMargin
            isNearStart -> cellMargin
            else -> xPos
        }

        val columnIndex = gridController.calculateColumnIndexWithRounding(xPosInternal)
        val rowIndex = gridController.calculateRowIndexWithRounding(yPos)

        val columnNumber = if (columnIndex == -1) -1 else columns[columnIndex].number
        val illegalTopRowIndex = -1
        val illegalBottomRowIndex = EditorSettings.FIELD_CONTENT_CELLS_COUNT + 1
        val rowNumber = if (rowIndex <= illegalTopRowIndex || rowIndex >= illegalBottomRowIndex) -1 else rowIndex

        loopsController.addDraggedLoop(rowNumber, columnNumber, loop, loopWidth, isOutFromVisibleBounds)
    }

    fun takeLoopFromField(xPos: Float, yPos: Float): FieldLoop? {
        val columnIndex = gridController.calculateColumnIndexPrecise(xPos, columns)
        val rowNumber = gridController.calculateRowIndexPrecise(yPos)

        return if (columnIndex != -1 && rowNumber != -1) {
            val columnNumber = columns[columnIndex].number
            loopsController.take(rowNumber, columnNumber)
        } else {
            null
        }
    }

    fun clearLoops() {
        loopsController.clear()
    }
}
