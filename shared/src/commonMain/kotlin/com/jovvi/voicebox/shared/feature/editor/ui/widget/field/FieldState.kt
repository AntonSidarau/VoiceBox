package com.jovvi.voicebox.shared.feature.editor.ui.widget.field

import com.jovvi.voicebox.shared.business.editor.model.Loop

class FieldState(
    val gridState: GridState
)

class GridState(
    var virtualStartXPosition: Float,
    val columns: Array<FieldColumn>
)

data class FieldLoop(
    val virtualStartXPos: Float,
    val virtualEndXPos: Float,
    val yPos: Float,
    val model: Loop,
    val columnNumber: Int,
    val rowNumber: Int
)

data class FieldColumn(
    var position: Float,
    var number: Int,
    val topBlendMode: BlendMode,
    val bottomBlendMode: BlendMode
) {

    enum class BlendMode(val fraction: Float) {
        LOW(0F),
        SEMI_MEDIUM(0.2F),
        MEDIUM(0.4F),
        SEMI_HIGH(0.6F),
        HIGH(0.8F);

        companion object {

            internal const val MIN_BLEND_VALUE = 0
            internal const val MAX_BLEND_VALUE = 4

            fun getFromBlendValue(value: Int): BlendMode {
                return when (value) {
                    0 -> LOW
                    1 -> SEMI_MEDIUM
                    2 -> MEDIUM
                    3 -> SEMI_HIGH
                    4 -> HIGH
                    else -> throw IllegalArgumentException("value must be between $MIN_BLEND_VALUE and $MAX_BLEND_VALUE")
                }
            }
        }
    }
}
