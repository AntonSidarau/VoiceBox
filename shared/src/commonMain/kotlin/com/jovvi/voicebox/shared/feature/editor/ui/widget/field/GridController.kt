package com.jovvi.voicebox.shared.feature.editor.ui.widget.field

import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator
import com.jovvi.voicebox.shared.feature.editor.helper.SharedStateHolder
import com.jovvi.voicebox.shared.feature.editor.ui.widget.field.FieldColumn.BlendMode
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.random.Random

internal class GridController(
    fieldWidth: Int,
    sizesCalculator: EditorSizesCalculator,
    private val sharedStateHolder: SharedStateHolder
) {

    init {
        sizesCalculator.ensureInitialized()
    }

    private val state: GridState

    // region shortcut values
    private var cellWidth: Float = 0F
    private var cellHeight: Float = 0F
    private var cellMargin: Float = 0F
    private var columnWidth: Float = 0F
    private var virtualStartPosBoundary: Float = 0F
    // endregion

    val columns: Array<FieldColumn> get() = state.columns
    val virtualStartPos: Float get() = state.virtualStartXPosition

    init {
        cellWidth = sizesCalculator.cellWidth
        cellHeight = sizesCalculator.cellHeight
        cellMargin = sizesCalculator.cellMargin
        columnWidth = cellWidth + cellMargin

        val maxLength = sizesCalculator.totalFieldWidth
        virtualStartPosBoundary = maxLength - fieldWidth

        val columnWidth = cellWidth + cellMargin
        val columnsCount = ceil(fieldWidth / columnWidth).toInt() + 1

        state = GridState(
            virtualStartXPosition = 0F,
            columns = Array(columnsCount) {
                val position = configureColumnPosition(it)
                val number = configureColumnNumber(it)

                FieldColumn(position, number, generateBlendMode(), generateBlendMode())
            }
        )
    }

    fun updateColumnsOnScroll(distanceX: Float): Boolean {
        if (isExceedsFieldBoundaries(distanceX)) {
            return false
        }

        val oldVirtualPosition = state.virtualStartXPosition
        val newVirtualPosition = oldVirtualPosition + distanceX
        var wasUpdatedNormally = true

        val deltaX = when {
            oldVirtualPosition > 0F && newVirtualPosition < 0F -> {
                wasUpdatedNormally = false
                -oldVirtualPosition
            }
            oldVirtualPosition <= virtualStartPosBoundary && newVirtualPosition > virtualStartPosBoundary -> {
                wasUpdatedNormally = false
                virtualStartPosBoundary - oldVirtualPosition
            }
            else -> distanceX
        }

        updateVirtualStartXPosition(deltaX)
        updateColumnsOnScrollInternal(deltaX)

        return wasUpdatedNormally
    }

    private fun updateColumnsOnScrollInternal(deltaX: Float) {
        val columnsCount = columns.size
        val distance = columnsCount * columnWidth
        val maxEndDistance = distance - columnWidth

        for (i in 0 until columnsCount) {
            val column = columns[i]
            val currentX = column.position
            val newX = currentX - deltaX

            val correctedX = when {
                newX < -cellWidth -> {
                    column.number += columnsCount
                    newX + distance
                }
                newX > maxEndDistance -> {
                    column.number -= columnsCount
                    newX - distance
                }
                else -> newX
            }
            column.position = correctedX
        }
    }

    fun calculateColumnIndex(xPos: Float): Int {
        val columns = state.columns
        val columnsCount = columns.size
        var index = -1
        for (i in 0 until columnsCount) {
            val j = if (i + 1 >= columnsCount) 0 else i + 1
            val current = columns[i].position
            val next = columns[j].position
            if (xPos in current..next) {
                val currentSide = xPos - current
                val nextSide = next - xPos
                index = if (currentSide > nextSide) j else i
                break
            }
        }

        return index
    }

    fun calculateRowIndexWithRounding(yPos: Float): Int {
        val possibleRow = ((yPos - cellHeight) / (cellHeight + cellMargin)).roundToInt()
        return if (possibleRow <= -1) -1 else possibleRow
    }

    fun calculateRowIndexPrecise(yPos: Float): Int {
        val possibleRow = (yPos - cellHeight - cellMargin) / (cellHeight + cellMargin)
        return if (possibleRow < 0F) -1 else possibleRow.toInt()
    }

    private fun configureColumnPosition(columnIndex: Int): Float {
        return (columnIndex - 1) * cellWidth + columnIndex * cellMargin
    }

    private fun configureColumnNumber(columnIndex: Int): Int {
        return columnIndex - 1
    }

    private fun generateBlendMode(): BlendMode {
        val generated = Random.nextInt(BlendMode.MIN_BLEND_VALUE, BlendMode.MAX_BLEND_VALUE + 1)
        return BlendMode.getFromBlendValue(generated)
    }

    private fun updateVirtualStartXPosition(deltaX: Float) {
        val newPosition = state.virtualStartXPosition + deltaX
        state.virtualStartXPosition = newPosition
        sharedStateHolder.fieldVirtualStartPos = newPosition
    }

    private fun isExceedsFieldBoundaries(deltaX: Float): Boolean {
        return when {
            state.virtualStartXPosition <= 0F && deltaX < 0 -> true
            state.virtualStartXPosition >= virtualStartPosBoundary && deltaX > 0 -> true
            else -> false
        }
    }
}
