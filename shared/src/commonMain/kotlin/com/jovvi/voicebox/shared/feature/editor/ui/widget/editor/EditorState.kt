package com.jovvi.voicebox.shared.feature.editor.ui.widget.editor

import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.feature.editor.EditorHelpers

class EditorState(
    var draggingLoopXPos: Float,
    var draggingLoopYPos: Float
) {

    var draggingLoop: Loop? = null
        private set
    var loopWidth: Float = 0F
        private set

    fun updateDraggingLoop(loop: Loop, fieldCellWidth: Float, cellMargin: Float) {
        draggingLoop = loop
        loopWidth = EditorHelpers.getLoopWidth(fieldCellWidth, cellMargin, loop.size)
    }

    fun removeDraggingLoop() {
        draggingLoop = null
    }
}
