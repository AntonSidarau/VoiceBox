package com.jovvi.voicebox.shared.feature.editor.ui.widget.field

import com.jovvi.voicebox.shared.feature.editor.EditorSettings
import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator
import kotlin.math.ceil
import kotlin.random.Random

private const val MIN_FACTOR = 0.3F
private const val MAX_FACTOR = 0.9F
private const val ROUND_VALUE = 100

internal class GridController(
    fieldWidth: Int,
    sizesCalculator: EditorSizesCalculator
) {

    private val state: GridState

    // region shortcut values
    private var cellWidth: Float = 0F
    private var cellMargin: Float = 0F
    private var columnWidth: Float = 0F
    private var virtualStartPosBoundary: Float = 0F
    // endregion

    val columns: Array<FieldColumn> get() = state.columns

    init {
        cellWidth = sizesCalculator.cellWidth
        cellMargin = sizesCalculator.cellMargin
        columnWidth = cellWidth + cellMargin

        val maxLength = EditorSettings.TIMELINE_LENGTH_COLUMNS * columnWidth + cellMargin
        virtualStartPosBoundary = maxLength - fieldWidth

        val columnWidth = cellWidth + cellMargin
        val columnsCount = ceil(fieldWidth / columnWidth).toInt() + 1

        state = GridState(
            virtualStartXPosition = 0F,
            columns = Array(columnsCount) {
                val position = configureColumnPosition(it)
                val number = configureColumnNumber(it)

                FieldColumn(position, number, generateBlendFactor(), generateBlendFactor())
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

        state.virtualStartXPosition += deltaX

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

    private fun configureColumnPosition(columnIndex: Int): Float {
        return (columnIndex - 1) * cellWidth + columnIndex * cellMargin
    }

    private fun configureColumnNumber(columnIndex: Int): Int {
        return columnIndex - 1
    }

    private fun generateBlendFactor(): Float {
        val generated = ceil(Random.nextFloat() * ROUND_VALUE) / ROUND_VALUE + MIN_FACTOR
        return generated.coerceAtMost(MAX_FACTOR)
    }

    private fun isExceedsFieldBoundaries(deltaX: Float): Boolean {
        return when {
            state.virtualStartXPosition <= 0F && deltaX < 0 -> true
            state.virtualStartXPosition >= virtualStartPosBoundary && deltaX > 0 -> true
            else -> false
        }
    }
}
