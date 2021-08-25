package com.jovvi.voicebox.shared.feature.editor.helper

import com.jovvi.voicebox.shared.feature.editor.EditorSettings
import com.jovvi.voicebox.shared.feature.editor.EditorSettings.FIELD_CELLS_COUNT
import com.jovvi.voicebox.shared.feature.editor.EditorSettings.PALETTE_CELLS_COUNT
import kotlin.math.roundToInt


class EditorSizesCalculator(private val sizeProvider: EditorComponentsSizeProvider) {

    companion object {

        const val UNDEFINED_SIZE = -1F

        private const val CELL_W_RATIO = 12F
        private const val CELL_H_RATIO = 25F
        private const val CELL_MARGINS_COUNT = FIELD_CELLS_COUNT - 1
    }

    var cellHeight: Float = UNDEFINED_SIZE
        private set

    val cellWidth: Float
        get() = (cellHeight * CELL_W_RATIO / CELL_H_RATIO).roundToInt().toFloat()

    val cellMargin: Float
        get() = sizeProvider.cellMargin.roundToInt().toFloat()

    val fieldHeight: Float
        get() = (cellHeight * FIELD_CELLS_COUNT + CELL_MARGINS_COUNT * sizeProvider.cellMargin)
            .roundToInt()
            .toFloat()

    val paletteHeight: Float
        get() = cellHeight * PALETTE_CELLS_COUNT + sizeProvider.paletteZoomHeight.roundToInt()

    val titleBarHeight: Float
        get() = sizeProvider.titleBarHeight.roundToInt().toFloat()

    val controlsBarHeight: Float
        get() = sizeProvider.controlsBarHeight.roundToInt().toFloat()

    val cellCornerRadius: Float
        get() = sizeProvider.cellCornerRadius.roundToInt().toFloat()

    fun calculate(totalHeight: Int) {
        val reservedContentHeight = calculateFixedContentHeight()
        val cellMarginsHeight = CELL_MARGINS_COUNT * sizeProvider.cellMargin
        val onlyCellsHeight = totalHeight - (reservedContentHeight + cellMarginsHeight)

        cellHeight = (onlyCellsHeight / EditorSettings.TOTAL_CELLS_COUNT).roundToInt().toFloat()
    }

    private fun calculateFixedContentHeight(): Float {
        return sizeProvider.titleBarHeight + sizeProvider.controlsBarHeight + sizeProvider.paletteZoomHeight
    }
}

// title - 100dp
// control bar 90dp
// palette - 56dp + 2 cell Height + 12bottom + 12bottom + 8top = 88dp + 2cellHeight
// field = 6cell + 3dp * 5 = 6cell + 15dp
