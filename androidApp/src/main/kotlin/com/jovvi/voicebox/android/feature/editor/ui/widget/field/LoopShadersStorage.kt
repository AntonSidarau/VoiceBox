package com.jovvi.voicebox.android.feature.editor.ui.widget.field

import android.graphics.LinearGradient
import android.graphics.Shader
import com.jovvi.voicebox.shared.business.editor.helper.LoopColorStorage
import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator

class LoopShadersStorage(
    private val loopColorStorage: LoopColorStorage,
    private val sizesCalculator: EditorSizesCalculator
) {

    private val loopShadersMap: MutableMap<Loop.Color, LinearGradient> = mutableMapOf()

    fun initialize() {
        loopShadersMap.clear()
        loopColorStorage.provideColors().forEach {
            loopShadersMap[it] = createLoopLinearGradient(it)
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
            sizesCalculator.getLoopWidth(Loop.Size.FOUR),
            0F,
            startColor,
            endColor,
            Shader.TileMode.CLAMP
        )
    }
}
