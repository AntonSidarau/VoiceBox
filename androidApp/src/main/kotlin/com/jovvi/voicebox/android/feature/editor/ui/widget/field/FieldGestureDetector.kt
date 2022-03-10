package com.jovvi.voicebox.android.feature.editor.ui.widget.field

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.FloatValueHolder
import com.jovvi.voicebox.shared.feature.editor.ui.widget.field.FieldStateController
import kotlin.math.abs

private const val FLING_FRACTION = 1.3F

class FieldGestureDetector(
    context: Context,
    private val stateController: FieldStateController,
    private val requestInvalidate: () -> Unit,
    private val requestInvalidateOnAnimation: () -> Unit
) : GestureDetector.OnGestureListener {

    private val maxFlingVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity

    override fun onDown(e: MotionEvent?): Boolean = true

    override fun onShowPress(e: MotionEvent?) {
        // TODO start drag
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        // TODO show action popup

        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        stateController.updateOnScroll(distanceX)
        requestInvalidate()
        return true
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
}
