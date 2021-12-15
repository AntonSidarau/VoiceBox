package com.jovvi.voicebox.shared.feature.editor.ui.widget.palette

import com.jovvi.voicebox.shared.business.editor.model.Loop

class PaletteState(
    var virtualStartPosition: Float,
    var magnifierCenterPosition: Float,
    val loopStorage: PaletteLoopStorage
)

data class PaletteLoop(
    val virtualXStartPosition: Float,
    val virtualXEndPosition: Float,
    val yTopPosition: Float,
    val yBottomPosition: Float,
    val isUpper: Boolean,
    val model: Loop
)
