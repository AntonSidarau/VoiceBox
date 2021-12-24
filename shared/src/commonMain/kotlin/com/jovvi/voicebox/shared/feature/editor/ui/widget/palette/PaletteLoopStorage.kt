package com.jovvi.voicebox.shared.feature.editor.ui.widget.palette

import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.feature.editor.EditorHelpers

private const val LOOP_WIDTH_OFFSET_PART = 4

class PaletteLoopStorage(
    private val horizontalMargin: Float,
    bottomMargin: Float,
    private val topMargin: Float,
    private val cellWidth: Float,
    private val cellMargin: Float,
    private val cellHeight: Float,
    private val paletteWidth: Float
) {

    private val betweenMargin: Float =
        EditorHelpers.getLoopWidth(cellWidth, cellMargin, Loop.Size.FOUR) / LOOP_WIDTH_OFFSET_PART
    private val secondRowOffset: Float = cellHeight + bottomMargin

    private val store: HashMap<Int, PaletteLoop> = hashMapOf()
    private val keyStore: MutableList<Int> = mutableListOf()
    private val lastLoop: PaletteLoop? get() = keyStore.lastOrNull()?.let { store.getValue(it) }

    val availableLoops: Collection<PaletteLoop> get() = store.values
    val scaleDownFactor: Float
        get() {
            val lastAvailableLoop = lastLoop ?: return 1F
            if (paletteWidth > lastAvailableLoop.virtualXEndPosition) return 1F

            val lastLoopXEndPosition = lastAvailableLoop.virtualXEndPosition
            val zoomWidth = paletteWidth - 2 * horizontalMargin

            return zoomWidth / lastLoopXEndPosition
        }

    fun addToEnd(loop: Loop) {
        val size = keyStore.size
        val key = size + 1

        val hasEvenLoopsCount = size % 2 == 0
        val halfSize = size / 2
        val rowIndexMargin = if (hasEvenLoopsCount) 0F else secondRowOffset
        val columnIndexMargin =
            horizontalMargin + halfSize * betweenMargin + calculateLoopsWidthInRow(hasEvenLoopsCount)

        val topYPosition = topMargin + rowIndexMargin
        val bottomYPosition = topYPosition + cellHeight
        val startXPosition = if (hasEvenLoopsCount) columnIndexMargin else columnIndexMargin + betweenMargin
        val endXPosition = startXPosition + EditorHelpers.getLoopWidth(cellWidth, cellMargin, loop.size)

        val paletteLoop = PaletteLoop(
            startXPosition, endXPosition, topYPosition, bottomYPosition, hasEvenLoopsCount, loop
        )
        addToStorage(key, paletteLoop)
    }

    fun getLoopFrom(xPos: Float, yPos: Float): PaletteLoop? {
        val topLoopsThreshold = topMargin + cellHeight + betweenMargin / 2
        val initialIndex = if (yPos <= topLoopsThreshold) 0 else 1
        val size = keyStore.size

        for (i in initialIndex until size step 2) {
            val key = keyStore[i]
            val loop = store[key]
            if (loop != null && xPos >= loop.virtualXStartPosition && xPos <= loop.virtualXEndPosition) {
                return loop
            }
        }

        return null
    }

    private fun calculateLoopsWidthInRow(hasEvenLoopsCount: Boolean): Float {
        val size = keyStore.size
        val initialIndex = if (hasEvenLoopsCount) 0 else 1
        var totalWidth = 0F
        for (i in initialIndex until size step 2) {
            val loop = store.getValue(keyStore[i])
            totalWidth += EditorHelpers.getLoopWidth(cellWidth, cellMargin, loop.model.size)
        }

        return totalWidth
    }

    private fun addToStorage(key: Int, loop: PaletteLoop) {
        store[key] = loop
        keyStore.add(key)
        keyStore.sort()
    }
}
