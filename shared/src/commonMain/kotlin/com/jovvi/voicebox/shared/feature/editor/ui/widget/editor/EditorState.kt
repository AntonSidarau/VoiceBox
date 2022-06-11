package com.jovvi.voicebox.shared.feature.editor.ui.widget.editor

import com.jovvi.voicebox.shared.business.editor.model.Loop

class EditorState(
    var draggingLoopXPos: Float,
    var draggingLoopYPos: Float
) {

    var draggingLoop: Loop? = null
        private set
    var loopWidth: Float = 0F
        private set

    fun updateDraggingLoop(loop: Loop, loopWidth: Float) {
        this.draggingLoop = loop
        this.loopWidth = loopWidth
    }

    fun removeDraggingLoop() {
        draggingLoop = null
        loopWidth = 0F
    }
}
