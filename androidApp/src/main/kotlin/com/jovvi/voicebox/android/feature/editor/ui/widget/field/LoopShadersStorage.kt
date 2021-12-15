package com.jovvi.voicebox.android.feature.editor.ui.widget.field

import android.graphics.LinearGradient
import android.graphics.Shader
import com.jovvi.voicebox.shared.business.editor.helper.LoopColorStorage
import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.feature.editor.EditorHelpers

class LoopShadersStorage(
    private val cellWidth: Float,
    private val cellHeight: Float,
    loopColorStorage: LoopColorStorage
) {

    private val loopShadersMap: Map<Loop.Color, LinearGradient>

    init {
        loopShadersMap = mutableMapOf<Loop.Color, LinearGradient>().apply {
            loopColorStorage.provideColors().forEach {
                put(it, createLoopLinearGradient(it))
            }
        }
    }

    fun getLoopShader(color: Loop.Color): LinearGradient {
        return loopShadersMap.getValue(color)
    }

    private fun createLoopLinearGradient(color: Loop.Color): LinearGradient {
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
