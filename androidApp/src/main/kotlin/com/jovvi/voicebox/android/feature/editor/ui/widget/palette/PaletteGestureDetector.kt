package com.jovvi.voicebox.android.feature.editor.ui.widget.palette

import android.view.GestureDetector
import android.view.MotionEvent
import com.jovvi.voicebox.shared.feature.editor.ui.widget.palette.PaletteStateController

class PaletteGestureDetector(
    private val stateController: PaletteStateController,
    private val requestInvalidate: () -> Unit,
) : GestureDetector.SimpleOnGestureListener() {

    private var isLoopDragging: Boolean = false

    // TODO if finger will be idle for a few seconds - scroll doesn't tracked
    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (e1 == null) return false

        return when {
            e1.y >= stateController.zoomedPaletteOffset -> {
                stateController.updateOnScroll(distanceX)
                requestInvalidate()
                true
            }
            isLoopDragging -> {
                stateController.handleLoopDragging(distanceX, distanceY)
                true
            }
            else -> false
        }
    }

    override fun onDown(e: MotionEvent?): Boolean {
        if (e == null) return false

        return if (e.y >= stateController.zoomedPaletteOffset) {
            handleTapOnZoomedArea(e)
            isLoopDragging = false
            true
        } else {
            handleTapOnLoopsArea(e)
            isLoopDragging = true
            true
        }
    }

    fun cancelLoopDragging() {
        isLoopDragging = false
    }

    private fun handleTapOnZoomedArea(e: MotionEvent) {
        stateController.moveTo(e.x)
        requestInvalidate()
    }

    private fun handleTapOnLoopsArea(e: MotionEvent) {
        stateController.handleLoopTouch(e.x, e.y)
    }
}
