package com.jovvi.voicebox.android.feature.editor.ui.widget.field

import android.graphics.LinearGradient
import android.graphics.Shader
import com.jovvi.voicebox.shared.feature.editor.ui.widget.field.FieldColumn.BlendMode

class CellShadersStorage(
    private val cellStartColor: Int,
    private val cellEndColor: Int,
    private val cellWidth: Float,
    private val cellHeight: Float
) {

    private val topCellShadersMap: Map<BlendMode, LinearGradient> = mapOf(
        BlendMode.LOW to createCellLinearGradient(BlendMode.LOW.fraction),
        BlendMode.SEMI_MEDIUM to createCellLinearGradient(BlendMode.SEMI_MEDIUM.fraction),
        BlendMode.MEDIUM to createCellLinearGradient(BlendMode.MEDIUM.fraction),
        BlendMode.SEMI_HIGH to createCellLinearGradient(BlendMode.SEMI_HIGH.fraction),
        BlendMode.HIGH to createCellLinearGradient(BlendMode.HIGH.fraction)
    )

    private val bottomCellShadersMap: Map<BlendMode, LinearGradient> = mapOf(
        BlendMode.LOW to createCellLinearGradient(BlendMode.LOW.fraction, isReversed = true),
        BlendMode.SEMI_MEDIUM to createCellLinearGradient(BlendMode.SEMI_MEDIUM.fraction, isReversed = true),
        BlendMode.MEDIUM to createCellLinearGradient(BlendMode.MEDIUM.fraction, isReversed = true),
        BlendMode.SEMI_HIGH to createCellLinearGradient(BlendMode.SEMI_HIGH.fraction, isReversed = true),
        BlendMode.HIGH to createCellLinearGradient(BlendMode.HIGH.fraction, isReversed = true)
    )

    fun getTopCellShader(blendMode: BlendMode): LinearGradient {
        return topCellShadersMap.getValue(blendMode)
    }

    fun getBottomCellShader(blendMode: BlendMode): LinearGradient {
        return bottomCellShadersMap.getValue(blendMode)
    }

    private fun createCellLinearGradient(fraction: Float, isReversed: Boolean = false): LinearGradient {
        val minRelativePosition = cellWidth / cellHeight
        val relativePosition = minRelativePosition + minRelativePosition * fraction
        val top = if (isReversed) 0F else cellHeight
        val bottom = if (isReversed) cellHeight else 0F

        return LinearGradient(
            0F,
            top,
            0F,
            bottom,
            intArrayOf(cellStartColor, cellEndColor),
            floatArrayOf(0F, relativePosition),
            Shader.TileMode.CLAMP
        )
    }
}
