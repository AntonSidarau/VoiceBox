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

    val totalFieldWidth: Float
        get() = EditorSettings.TIMELINE_LENGTH_COLUMNS * (cellWidth + cellMargin) + cellMargin

    val paletteHeight: Float
        get() {
            val cellsHeight = cellHeight * PALETTE_CELLS_COUNT
            val zoomHeight = zoomedPaletteHeight
            val margins = paletteMarginsHeight

            return cellsHeight + zoomHeight + margins
        }

    val titleBarHeight: Float
        get() = sizeProvider.titleBarHeight.roundToInt().toFloat()

    val controlsBarHeight: Float
        get() = sizeProvider.controlsBarHeight.roundToInt().toFloat()

    val cellCornerRadius: Float
        get() = sizeProvider.cellCornerRadius.roundToInt().toFloat()

    val paletteHorizontalMargin: Float
        get() = sizeProvider.paletteHorizontalMargin.roundToInt().toFloat()

    val paletteInnerVerticalMargin: Float
        get() = sizeProvider.paletteInnerVerticalMargin.roundToInt().toFloat()

    val paletteOuterVerticalMargin: Float
        get() = sizeProvider.paletteOuterVerticalMargin.roundToInt().toFloat()

    val zoomedPaletteHeight: Float
        get() = sizeProvider.paletteZoomHeight.roundToInt().toFloat()

    val zoomedPaletteBorderWidth: Float
        get() = sizeProvider.zoomedPaletteBorderWidth.roundToInt().toFloat()

    private val paletteMarginsHeight: Float
        get() = 2 * (paletteInnerVerticalMargin + paletteOuterVerticalMargin)

    fun calculate(totalHeight: Int) {
        val reservedContentHeight = calculateFixedContentHeight()
        val cellMarginsHeight = CELL_MARGINS_COUNT * sizeProvider.cellMargin
        val onlyCellsHeight = totalHeight - (reservedContentHeight + cellMarginsHeight)

        cellHeight = (onlyCellsHeight / EditorSettings.TOTAL_CELLS_COUNT).roundToInt().toFloat()
    }

    private fun calculateFixedContentHeight(): Float {
        return sizeProvider.titleBarHeight +
                sizeProvider.controlsBarHeight +
                sizeProvider.paletteZoomHeight +
                paletteMarginsHeight
    }
}

// title - 96dp
// control bar 86dp
// palette - 56dp + 2 cell Height + 12bottom + 12bottom + 8top + 8bot = 96dp + 2cellHeight
// field = 6cell + 3dp * 5 = 6cell + 15dp
