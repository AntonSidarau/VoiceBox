package com.jovvi.voicebox.android.feature.editor.ui.widget.palette

import android.view.GestureDetector
import android.view.MotionEvent
import com.jovvi.voicebox.shared.feature.editor.ui.widget.palette.PaletteStateController

class PaletteGestureDetector(
    private val stateController: PaletteStateController,
    private val requestInvalidate: () -> Unit,
) : GestureDetector.SimpleOnGestureListener() {

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (e1 != null && e1.y >= stateController.zoomedPaletteOffset) {
            stateController.updateOnScroll(distanceX)
            requestInvalidate()
            return true
        }

        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        if (e != null && e.y >= stateController.zoomedPaletteOffset) {
            stateController.moveTo(e.x)
            requestInvalidate()
            return true
        }

        return false
    }
}
