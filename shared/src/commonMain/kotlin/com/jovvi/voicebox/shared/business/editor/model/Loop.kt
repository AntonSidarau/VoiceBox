package com.jovvi.voicebox.shared.business.editor.model

import com.jovvi.voicebox.shared.common.model.ColorMpp

data class Loop(
    val name: String,
    val color: Color,
    val size: Size
) {

    data class Color(val startColor: ColorMpp, val endColor: ColorMpp)

    enum class Size(val value: Int) {
        TWO(2),
        FOUR(4);
    }
}
