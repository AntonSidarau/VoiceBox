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

            val key = loopStorage.calculateKey(rowNumber, numberToPut)
            val hasCandidateToReplace = loopStorage.getLoopByKey(key)
            if (hasCandidateToReplace != null) {
                loopStorage.remove(key)
            }

            addDraggedLoopInternal(rowNumber, numberToPut, loop, loopWidth)
        } else {
            loopStorage.tryRestoreExtractedLoop()
        }
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
            loopStorage.tryRestoreExtractedLoop()
        }
    }
}
