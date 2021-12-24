package com.jovvi.voicebox.android.feature.editor.ui.widget.field

import android.graphics.LinearGradient
import android.graphics.Shader
import com.jovvi.voicebox.shared.business.editor.helper.LoopColorStorage
import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.feature.editor.EditorHelpers

class LoopShadersStorage(
    private val loopColorStorage: LoopColorStorage
) {

    private val loopShadersMap: MutableMap<Loop.Color, LinearGradient> = mutableMapOf()

    fun initialize(cellWidth: Float, cellHeight: Float) {
        loopShadersMap.clear()
        loopColorStorage.provideColors().forEach {
            loopShadersMap[it] = createLoopLinearGradient(it, cellWidth, cellHeight)
        }
    }

    fun getLoopShader(color: Loop.Color): LinearGradient {
        return loopShadersMap.getValue(color)
    }

    private fun createLoopLinearGradient(
        color: Loop.Color,
        cellWidth: Float,
        cellHeight: Float
    ): LinearGradient {
        val startColor = color.startColor.argb
        val endColor = color.endColor.argb

        return LinearGradient(
            0F,
            0F,
            EditorHelpers.getLoopWidth(cellWidth, cellHeight, Loop.Size.FOUR),
            0F,
            startColor,
            endColor,
            Shader.TileMode.CLAMP
        )
    }
}
