package com.jovvi.voicebox.shared.feature.editor.ui.widget.palette

import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.common.SimpleLogger
import com.jovvi.voicebox.shared.feature.editor.EditorSettings.PALETTE_CELLS_COUNT
import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator

private const val ZOOM_MARGINS_COUNT = 3

class PaletteStateController(
    private val sizesCalculator: EditorSizesCalculator,
    private val simpleLogger: SimpleLogger // TODO remove
) {

    companion object {

        const val MAGNIFIER_UNINITIALIZED = -1F
    }

    private lateinit var state: PaletteState

    val loops: Collection<PaletteLoop> get() = state.loopStorage.availableLoops
    val paletteScaleX: Float get() = state.loopStorage.scaleDownFactor
    val virtualStartPos: Float get() = state.virtualStartPosition
    val magnifierCenterPos: Float get() = state.magnifierCenterPosition

    var paletteHorizontalMargin: Float = 0F
        private set
    var paletteTotalWidth: Float = 0F
        private set

    var zoomedPaletteOffset: Float = 0F
        private set
    var zoomedPaletteInnerMargin: Float = 0F
        private set
    var zoomedPaletteCellHeight: Float = 0F
        private set
    var zoomedPaletteHorizontalMargin: Float = 0F
        private set
    var zoomedPaletteVerticalMargin: Float = 0F
        private set

    var magnifierHalfWidth: Float = MAGNIFIER_UNINITIALIZED
        private set

    fun initialize(fieldWidth: Float) {
        val innerPaletteHorizontalMargin = sizesCalculator.paletteHorizontalMargin
        val bottomMargin = sizesCalculator.paletteInnerVerticalMargin
        val topMargin = sizesCalculator.paletteOuterVerticalMargin
        val cellWidth = sizesCalculator.cellWidth
        val cellMargin = sizesCalculator.cellMargin
        val cellHeight = sizesCalculator.cellHeight

        paletteTotalWidth = fieldWidth
        paletteHorizontalMargin = 2 * innerPaletteHorizontalMargin
        zoomedPaletteOffset = topMargin + 2 * (cellHeight + bottomMargin)
        zoomedPaletteInnerMargin = topMargin
        val zoomedPaletteMargins = ZOOM_MARGINS_COUNT * zoomedPaletteInnerMargin
        zoomedPaletteCellHeight = (sizesCalculator.zoomedPaletteHeight - zoomedPaletteMargins) / PALETTE_CELLS_COUNT

        val borderWidth = sizesCalculator.zoomedPaletteBorderWidth
        zoomedPaletteVerticalMargin = borderWidth / 2
        zoomedPaletteHorizontalMargin = paletteHorizontalMargin / 2 + zoomedPaletteVerticalMargin

        val loopStorage = PaletteLoopStorage(
            paletteHorizontalMargin, bottomMargin, topMargin, cellWidth, cellMargin, cellHeight, fieldWidth
        )

        state = PaletteState(
            virtualStartPosition = 0F,
            magnifierCenterPosition = MAGNIFIER_UNINITIALIZED,
            loopStorage = loopStorage
        )
    }

    fun updateOnScroll(distanceX: Float) {
        val newPosition = state.magnifierCenterPosition - distanceX
        state.magnifierCenterPosition = correctMagnifierPosition(newPosition)
        state.virtualStartPosition = calculateVirtualPalettePositionFrom(state.magnifierCenterPosition)
    }

    fun moveTo(xPos: Float) {
        state.magnifierCenterPosition = correctMagnifierPosition(xPos)
        state.virtualStartPosition = calculateVirtualPalettePositionFrom(state.magnifierCenterPosition)
    }

    fun prePopulate(loops: List<Loop>) {
        loops.forEach { state.loopStorage.addToEnd(it) }
    }

    fun initializeMagnifierWidth(width: Float) {
        magnifierHalfWidth = width / 2
        state.magnifierCenterPosition = magnifierHalfWidth
    }

    private fun calculateVirtualPalettePositionFrom(magnifierPosition: Float): Float {
        return (magnifierPosition - magnifierHalfWidth) / paletteScaleX
    }

    private fun correctMagnifierPosition(toPosition: Float): Float {
        val startBoundary = magnifierHalfWidth
        val endBoundary = paletteTotalWidth - startBoundary - 2 * zoomedPaletteHorizontalMargin

        return when {
            toPosition <= startBoundary -> startBoundary
            toPosition >= endBoundary -> endBoundary
            else -> toPosition
        }
    }
}
