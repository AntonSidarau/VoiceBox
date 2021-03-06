package com.jovvi.voicebox.android.feature.editor.ui.widget.field

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator

class LoopDrawer(
    private val sizesCalculator: EditorSizesCalculator,
    private val shadersStorage: LoopShadersStorage
) {

    init {
        sizesCalculator.ensureInitialized()
    }

    private val loopHeight = sizesCalculator.cellHeight

    private val rect: RectF = RectF()
    private val gradientPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rawPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cornerRadius = sizesCalculator.cellCornerRadius

    fun draw(canvas: Canvas, xPos: Float, yPos: Float, loop: Loop) {
        canvas.save()
        canvas.translate(xPos, yPos)

        val loopWidth = sizesCalculator.getLoopWidth(loop.size)
        rect.set(0F, 0F, loopWidth, loopHeight)
        gradientPaint.shader = shadersStorage.getLoopShader(loop.color)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, gradientPaint)

        canvas.restore()
    }

    fun drawWithNoShader(
        canvas: Canvas,
        xStart: Float,
        xEnd: Float,
        yStart: Float,
        yEnd: Float,
        loop: Loop
    ) {
        rect.set(xStart, yStart, xEnd, yEnd)
        rawPaint.color = loop.color.startColor.argb
        canvas.drawRect(rect, rawPaint)
    }
}
