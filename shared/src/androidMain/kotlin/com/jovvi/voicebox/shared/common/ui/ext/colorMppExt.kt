package com.jovvi.voicebox.shared.common.ui.ext


import androidx.annotation.ColorInt
import com.jovvi.voicebox.shared.common.model.ColorMpp

@ColorInt
fun ColorMpp.colorInt(): Int {
    return argb.toInt()
}
