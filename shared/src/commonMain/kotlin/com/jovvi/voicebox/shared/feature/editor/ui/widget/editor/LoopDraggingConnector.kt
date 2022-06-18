package com.jovvi.voicebox.shared.feature.editor.ui.widget.editor

import com.jovvi.voicebox.shared.business.editor.model.Loop

interface LoopDraggingConnector {

    fun setOnStartDraggingLoopListener(block: (xPos: Float, yPos: Float, loop: Loop) -> Unit)

    fun setOnEndDraggingLoopListener(block: () -> Unit)

    fun setOnDraggingLoopListener(block: (distanceX: Float, distanceY: Float) -> Unit)

    fun clearListeners()

    companion object {

        fun default(dragController: EditorDragController): LoopDraggingConnector {
            return object : LoopDraggingConnector {
                override fun setOnStartDraggingLoopListener(block: (xPos: Float, yPos: Float, loop: Loop) -> Unit) {
                    dragController.setOnStartDraggingLoopListener(block)
                }

                override fun setOnEndDraggingLoopListener(block: () -> Unit) {
                    dragController.setOnEndDraggingLoopListener(block)
                }

                override fun setOnDraggingLoopListener(block: (distanceX: Float, distanceY: Float) -> Unit) {
                    dragController.setOnDraggingLoopListener(block)
                }

                override fun clearListeners() {
                    with(dragController) {
                        setOnStartDraggingLoopListener(null)
                        setOnEndDraggingLoopListener(null)
                        setOnDraggingLoopListener(null)
                    }
                }
            }
        }
    }
}
