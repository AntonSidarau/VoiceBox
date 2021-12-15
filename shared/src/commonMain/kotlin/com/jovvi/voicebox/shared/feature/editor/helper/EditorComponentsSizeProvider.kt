package com.jovvi.voicebox.shared.feature.editor.helper

interface EditorComponentsSizeProvider {

    val totalWidth: Float

    val cellMargin: Float

    val cellCornerRadius: Float

    val titleBarHeight: Float

    val controlsBarHeight: Float

    val paletteZoomHeight: Float

    val paletteInnerVerticalMargin: Float

    val paletteOuterVerticalMargin: Float

    val paletteHorizontalMargin: Float

    val zoomedPaletteBorderWidth: Float
}
