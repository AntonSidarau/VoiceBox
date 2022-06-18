package com.jovvi.voicebox.shared.feature.editor.ui.widget.editor

import com.jovvi.voicebox.shared.business.editor.model.Loop

class EditorDragController {

    var isLoopDragging = false
        private set

    private var onStartDraggingLoopListener: ((xPos: Float, yPos: Float, loop: Loop) -> Unit)? = null
    private var onEndDraggingLoopListener: (() -> Unit)? = null
    private var onDraggingLoopListener: ((distanceX: Float, distanceY: Float) -> Unit)? = null

    fun setOnStartDraggingLoopListener(block: ((xPos: Float, yPos: Float, loop: Loop) -> Unit)?) {
        onStartDraggingLoopListener = block
    }

    fun setOnDraggingLoopListener(block: ((distanceX: Float, distanceY: Float) -> Unit)?) {
        onDraggingLoopListener = block
    }

    fun setOnEndDraggingLoopListener(block: (() -> Unit)?) {
        onEndDraggingLoopListener = block
    }

    fun startDragging(xPos: Float, yPos: Float, loop: Loop) {
        isLoopDragging = true
        onStartDraggingLoopListener?.invoke(xPos, yPos, loop)
    }

    fun updateDragging(deltaX: Float, deltaY: Float) {
        onDraggingLoopListener?.invoke(deltaX, deltaY)
    }

    fun endDragging() {
        isLoopDragging = false
        onEndDraggingLoopListener?.invoke()
    }
}
