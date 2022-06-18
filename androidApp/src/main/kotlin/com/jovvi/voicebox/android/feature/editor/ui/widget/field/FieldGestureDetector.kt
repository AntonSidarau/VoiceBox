package com.jovvi.voicebox.android.feature.editor.ui.widget.field

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.FloatValueHolder
import com.jovvi.voicebox.shared.feature.editor.ui.widget.editor.EditorDragController
import com.jovvi.voicebox.shared.feature.editor.ui.widget.field.FieldLoop
import com.jovvi.voicebox.shared.feature.editor.ui.widget.field.FieldStateController
import kotlin.math.abs

private const val FLING_FRACTION = 1.3F

class FieldGestureDetector(
    context: Context,
    private val stateController: FieldStateController,
    private val dragController: EditorDragController,
    private val requestInvalidate: () -> Unit,
    private val requestInvalidateOnAnimation: () -> Unit
) : GestureDetector.OnGestureListener {

    private val maxFlingVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity

    override fun onDown(e: MotionEvent?) = true // TODO call context menu

    override fun onShowPress(e: MotionEvent?) {
        if (e == null) return
        if (dragController.isLoopDragging) return
        if (!stateController.isInContentBounds(e.y)) return

        val fieldLoop: FieldLoop? = stateController.takeLoopFromField(e.x, e.y)
        if (fieldLoop != null) {
            val xPos = fieldLoop.virtualStartXPos - stateController.virtualStartPos
            val yPos = fieldLoop.yPos
            dragController.startDragging(xPos, yPos, fieldLoop.model)
            requestInvalidate()
        }
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        // TODO show action popup

        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return when {
            e1 == null -> false
            dragController.isLoopDragging -> {
                dragController.updateDragging(distanceX, distanceY)
                true
            }
            else -> {
                stateController.updateOnScroll(distanceX)
                requestInvalidate()
                true
            }
        }

    }

    override fun onLongPress(e: MotionEvent?) = Unit

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        val sign = if (velocityX > 0) -1 else 1
        val absVelocity = abs(velocityX)

        FlingAnimation(FloatValueHolder()).apply {
            setStartVelocity(absVelocity)
            friction = FLING_FRACTION
            addUpdateListener { _, _, velocity ->
                val normalizedVelocity = stateController.normalizeVelocity(velocity, maxFlingVelocity)
                val isNormalUpdate = stateController.updateOnScroll(normalizedVelocity * sign)
                requestInvalidateOnAnimation()
                if (!isNormalUpdate) {
                    cancel()
                }
            }
            start()
        }

        return true
    }

    fun cancelLoopDragging() {
        dragController.endDragging()
    }
}
