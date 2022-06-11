package com.jovvi.voicebox.android.feature.editor.ui.widget.field

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import com.jovvi.voicebox.android.R
import com.jovvi.voicebox.android.common.di.Injector
import com.jovvi.voicebox.android.feature.editor.di.EditorComponent
import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.common.ui.ext.dpToPx
import com.jovvi.voicebox.shared.common.ui.ext.getFont
import com.jovvi.voicebox.shared.common.ui.ext.getThemeColor
import com.jovvi.voicebox.shared.common.ui.ext.spToPx
import com.jovvi.voicebox.shared.feature.editor.EditorSettings
import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator
import com.jovvi.voicebox.shared.feature.editor.ui.widget.field.FieldColumn
import com.jovvi.voicebox.shared.feature.editor.ui.widget.field.FieldStateController

private const val TEXT_SIZE_SP = 12F
private const val DOT_RADIUS_DP = 3F

class EditorFieldView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val stateController: FieldStateController
    private val sizesCalculator: EditorSizesCalculator
    private val loopShadersStorage: LoopShadersStorage

    init {
        val component: EditorComponent = Injector.getComponent(EditorComponent::class.java)
        stateController = component.fieldStateController
        sizesCalculator = component.sizesCalculator
        loopShadersStorage = component.loopShadersStorage
    }

    private val cellBackgroundColor = context.getThemeColor(R.attr.colorSecondary)
    private val defaultOnCellBackgroundColor = context.getThemeColor(R.attr.colorOnSurface)
    private val fieldBackgroundColor = context.getThemeColor(android.R.attr.colorBackground)

    private val dotRadius = context.dpToPx(DOT_RADIUS_DP)

    private val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = cellBackgroundColor
        pathEffect = CornerPathEffect(sizesCalculator.cellCornerRadius)
        strokeCap = Paint.Cap.ROUND
    }
    private val columnNumberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = defaultOnCellBackgroundColor
        textSize = context.spToPx(TEXT_SIZE_SP)
        typeface = context.getFont(R.font.rubik_medium)
    }
    private val blendedCellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        pathEffect = CornerPathEffect(sizesCalculator.cellCornerRadius)
        strokeCap = Paint.Cap.ROUND
    }

    private val columnTemplatePath: Path = Path()
    private val columnPath: Path = Path()
    private val blendedColumnCellPathTemplate: Path = Path()
    private val blendedColumnCellPath: Path = Path()
    private val columnNumberRect: RectF = RectF()

    private val gestureDetector: GestureDetectorCompat = GestureDetectorCompat(
        context, FieldGestureDetector(
            context = context,
            stateController = stateController,
            requestInvalidate = { invalidate() },
            requestInvalidateOnAnimation = { postInvalidateOnAnimation() }
        )
    )

    private lateinit var cellShadersStorage: CellShadersStorage
    private lateinit var loopDrawer: LoopDrawer

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        stateController.initialize(w)

        val cellHeight = sizesCalculator.cellHeight
        val cellWidth = sizesCalculator.cellWidth
        val cellMargin = sizesCalculator.cellMargin

        initBlendedColumnCellTemplatePath(cellHeight, cellWidth)
        initColumnTemplatePath(cellHeight, cellWidth, cellMargin)
        initColumnNumberRect(cellHeight, cellWidth, cellMargin)
        cellShadersStorage = CellShadersStorage(
            cellBackgroundColor, fieldBackgroundColor, cellWidth, cellHeight
        )
        initLoopDrawer()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)

        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawColumns(canvas)
        drawLoops(canvas)
    }

    fun tryAddLoop(xPos: Float, yPos: Float, loop: Loop) {
        stateController.addDraggedLoop(xPos, yPos, loop)
        invalidate()
    }

    fun clearLoops() {
        stateController.clearLoops()
        invalidate()
    }

    private fun drawColumns(canvas: Canvas) {
        val columns = stateController.columns
        for (column in columns) {
            blendedColumnCellPathTemplate.offset(column.position, 0F, blendedColumnCellPath)
            blendedCellPaint.shader = cellShadersStorage.getTopCellShader(column.topBlendMode)
            canvas.drawPath(blendedColumnCellPath, blendedCellPaint)

            columnTemplatePath.offset(column.position, 0F, columnPath)
            canvas.drawPath(columnPath, cellPaint)

            blendedCellPaint.shader = cellShadersStorage.getBottomCellShader(column.bottomBlendMode)
            canvas.save()
            canvas.translate(0F, stateController.bottomNotContentCellPosition)
            canvas.drawPath(blendedColumnCellPath, blendedCellPaint)
            canvas.restore()

            val offsetTop = columnNumberRect.top
            val offsetBottom = stateController.fieldHeight - columnNumberRect.bottom
            val columnNumber = column.number
            if (columnNumber % EditorSettings.COLUMN_STEP == 0) {
                val text = (columnNumber / EditorSettings.COLUMN_STEP + 1).toString()
                drawColumnNumber(canvas, offsetTop, text, column)
                drawColumnNumber(canvas, offsetBottom, text, column)
            } else {
                drawDot(canvas, offsetTop, column)
                drawDot(canvas, offsetBottom, column)
            }
        }
    }

    private fun drawColumnNumber(canvas: Canvas, offsetTop: Float, text: String, column: FieldColumn) {
        val textWidth = cellPaint.measureText(text)
        val paintOffset = (columnNumberPaint.descent() + columnNumberPaint.ascent()) / 2F
        val yPos = columnNumberRect.height() / 2F - paintOffset + offsetTop
        val xPos = columnNumberRect.width() / 2F + column.position - textWidth

        val color = defaultOnCellBackgroundColor
        columnNumberPaint.color = color
        canvas.drawText(text, xPos - textWidth / 2, yPos, columnNumberPaint)
    }

    private fun drawDot(canvas: Canvas, offsetTop: Float, column: FieldColumn) {
        val centerX = columnNumberRect.width() / 2F + column.position
        val centerY = columnNumberRect.height() / 2 + offsetTop

        val color = defaultOnCellBackgroundColor
        columnNumberPaint.color = color
        canvas.drawCircle(centerX, centerY, dotRadius, columnNumberPaint)
    }

    private fun drawLoops(canvas: Canvas) {
        val loops = stateController.loops
        val startBoundary = stateController.virtualStartPos
        val endBoundary = startBoundary + width

        for (loop in loops) {
            // TODO check if loop clicked
            if (loop.virtualStartXPos <= endBoundary && loop.virtualEndXPos >= startBoundary) {
                val xPos = loop.virtualStartXPos - startBoundary
                val loopModel = loop.model

                loopDrawer.draw(canvas, xPos, loop.yPos, loopModel)
            }
        }
    }

    private fun initBlendedColumnCellTemplatePath(cellHeight: Float, cellWidth: Float) {
        blendedColumnCellPathTemplate.apply {
            reset()
            moveTo(0F, 0F)
            rLineTo(cellWidth, 0F)
            rLineTo(0F, cellHeight)
            rLineTo(-cellWidth, 0F)
            rLineTo(0F, -cellHeight)
            close()
        }
    }

    private fun initColumnTemplatePath(cellHeight: Float, cellWidth: Float, cellMargin: Float) {
        columnTemplatePath.apply {
            reset()
            for (i in 1..EditorSettings.FIELD_CONTENT_CELLS_COUNT) {
                val offset = i * cellHeight + i * cellMargin
                moveTo(0F, offset)
                rLineTo(cellWidth, 0F)
                rLineTo(0F, cellHeight)
                rLineTo(-cellWidth, 0F)
                rLineTo(0F, -cellHeight)
                close()
            }
        }
    }

    private fun initColumnNumberRect(cellHeight: Float, cellWidth: Float, cellMargin: Float) {
        columnNumberRect.set(
            cellMargin,
            cellHeight - cellWidth,
            cellWidth + cellMargin,
            cellHeight
        )
    }

    private fun initLoopDrawer() {
        loopDrawer = LoopDrawer(sizesCalculator, loopShadersStorage)
    }
}
