package com.jovvi.voicebox.shared.feature.editor.ui.widget.field

import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator

private const val PIXELS_PER_SECOND = 200

class FieldStateController {

    private lateinit var gridController: GridController

    var fieldHeight: Float = 0F
        private set

    val columns: Array<FieldColumn> get() = gridController.columns

    fun initialize(
        fieldWidth: Int,
        sizesCalculator: EditorSizesCalculator
    ) {
        fieldHeight = sizesCalculator.fieldHeight

        gridController = GridController(fieldWidth, sizesCalculator)
        // TODO add another controllers, such as timelineController, draggedLoopController, etc
    }

    fun updateOnScroll(distanceX: Float): Boolean {
        return gridController.updateColumnsOnScroll(distanceX)
    }

    fun normalizeVelocity(velocity: Float, maxFlingVelocity: Int): Float {
        return velocity / maxFlingVelocity * PIXELS_PER_SECOND
    }
}