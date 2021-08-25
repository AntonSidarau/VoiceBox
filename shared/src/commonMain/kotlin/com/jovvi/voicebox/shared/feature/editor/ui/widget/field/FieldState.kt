package com.jovvi.voicebox.shared.feature.editor.ui.widget.field

class FieldState(
    val gridState: GridState
)

class GridState(
    var virtualStartXPosition: Float,
    val columns: Array<FieldColumn>
)

data class FieldColumn(
    var position: Float,
    var number: Int,
    val topBlendFactor: Float,
    val bottomBlendFactor: Float
)
