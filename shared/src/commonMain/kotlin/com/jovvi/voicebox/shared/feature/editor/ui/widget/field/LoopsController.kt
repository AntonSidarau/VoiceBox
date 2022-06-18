package com.jovvi.voicebox.shared.feature.editor.ui.widget.field

import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator

class LoopsController(sizesCalculator: EditorSizesCalculator) {

    init {
        sizesCalculator.ensureInitialized()
    }

    private val loopStorage = FieldLoopStorage(
        sizesCalculator.cellWidth,
        sizesCalculator.cellHeight,
        sizesCalculator.cellMargin
    )

    val loops: Collection<FieldLoop> get() = loopStorage.availableLoops

    fun addDraggedLoop(
        rowNumber: Int,
        columnNumber: Int,
        loop: Loop,
        loopWidth: Float,
        isOutFromVisibleBounds: Boolean
    ) {
        val isInBounds = columnNumber != -1 && rowNumber != -1
        if (isInBounds) {
            val numberToPut = if (isOutFromVisibleBounds) columnNumber - loop.size.value else columnNumber

            val hasCandidateToReplace = loopStorage.getLoopBy(rowNumber, numberToPut)
            if (hasCandidateToReplace != null) {
                loopStorage.remove(rowNumber, numberToPut)
            }

            addDraggedLoopInternal(rowNumber, numberToPut, loop, loopWidth)
        } else {
            loopStorage.tryReturnTakenLoop()
        }
    }

    fun take(rowNumber: Int, columnNumber: Int): FieldLoop? {
        return loopStorage.take(rowNumber, columnNumber)
    }

    fun clear() {
        loopStorage.clear()
    }

    private fun addDraggedLoopInternal(
        rowNumber: Int,
        columnNumber: Int,
        loop: Loop,
        loopWidth: Float
    ) {
        if (columnNumber >= 0 &&
            loopStorage.isAllowedToPutLoop(rowNumber, columnNumber, loop.size)
        ) {
            loopStorage.put(rowNumber, columnNumber, loop, loopWidth)
        } else {
            loopStorage.tryReturnTakenLoop()
        }
    }
}
