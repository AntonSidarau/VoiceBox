package com.jovvi.voicebox.shared.feature.editor

import com.jovvi.voicebox.shared.business.editor.model.Loop
import kotlin.math.roundToInt

object EditorHelpers {

    fun getLoopWidth(cellWidth: Float, cellMargin: Float, loopSize: Loop.Size): Float {
        return (cellWidth * loopSize.value + (loopSize.value - 1) * cellMargin).roundToInt().toFloat()
    }
}
