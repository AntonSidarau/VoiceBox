package com.jovvi.voicebox.android.feature.editor.ui.widget.palette

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import com.jovvi.voicebox.android.R
import com.jovvi.voicebox.android.common.di.Injector
import com.jovvi.voicebox.android.feature.editor.di.EditorComponent
import com.jovvi.voicebox.android.feature.editor.ui.widget.field.LoopDrawer
import com.jovvi.voicebox.android.feature.editor.ui.widget.field.LoopShadersStorage
import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.common.ui.ext.dpToPx
import com.jovvi.voicebox.shared.common.ui.ext.getThemeColor
import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator
import com.jovvi.voicebox.shared.feature.editor.ui.widget.editor.EditorDragController
import com.jovvi.voicebox.shared.feature.editor.ui.widget.editor.LoopDraggingConnector
import com.jovvi.voicebox.shared.feature.editor.ui.widget.palette.PaletteStateController

private const val ZOOM_BORDER_WIDTH_DP = 2F

class EditorPaletteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val dragController: EditorDragController = Injector
        .getComponent<EditorComponent>(EditorComponent::class.java)
        .editorDragController
) : View(context, attrs, defStyleAttr),
    LoopDraggingConnector by LoopDraggingConnector.default(dragController) {

    private val stateController: PaletteStateController
    private val sizesCalculator: EditorSizesCalculator
    private val loopShadersStorage: LoopShadersStorage

    init {
        val component: EditorComponent = Injector.getComponent(EditorComponent::class.java)
        stateController = component.paletteStateController
        sizesCalculator = component.sizesCalculator
        loopShadersStorage = component.loopShadersStorage
    }

    private val paletteGesturesListener = PaletteGestureDetector(
        stateController = stateController,
        dragController = dragController,
        requestInvalidate = { invalidate() }
    )
    private val gestureDetector: GestureDetectorCompat = GestureDetectorCompat(
        context, paletteGesturesListener
    )

    private val magnifierRect: RectF = RectF()
    private val magnifierPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = context.dpToPx(ZOOM_BORDER_WIDTH_DP)
        color = context.getThemeColor(R.attr.colorSecondaryVariant)
    }
    private val paletteBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = context.dpToPx(ZOOM_BORDER_WIDTH_DP)
        color = context.getThemeColor(R.attr.colorSecondary)
    }

    private var zoomBorderRadius: Float = 0F

    private lateinit var loopDrawer: LoopDrawer
    private lateinit var zoomedPaletteBitmap: Bitmap
    private lateinit var zoomedPaletteCanvas: Canvas

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        stateController.initialize(w.toFloat())

        val zoomPaletteHeight = sizesCalculator.zoomedPaletteHeight
        zoomBorderRadius = sizesCalculator.cellCornerRadius

        initLoopsShaderDrawer()
        initZoomedPaletteBitmap(w, zoomPaletteHeight.toInt())
        initMagnifierRect(zoomPaletteHeight)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)

        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            paletteGesturesListener.cancelLoopDragging()
        }

        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawPaletteLoops(canvas)
        drawZoomedPalette(canvas)
        drawMagnifier(canvas)
    }

    fun prePopulate(loops: List<Loop>) {
        stateController.prePopulate(loops)
        drawZoomedPaletteContent()
        invalidate()
    }

    private fun drawPaletteLoops(canvas: Canvas) {
        val loops = stateController.loops
        val startBoundary = stateController.virtualStartPos
        val endBoundary = startBoundary + width

        for (loop in loops) {
            if (loop.virtualXStartPosition <= endBoundary && loop.virtualXEndPosition >= startBoundary) {
                val xPos = loop.virtualXStartPosition - startBoundary
                loopDrawer.draw(canvas, xPos, loop.yTopPosition, loop.model)
            }
        }
    }

    private fun drawZoomedPalette(canvas: Canvas) {
        canvas.drawBitmap(zoomedPaletteBitmap, 0F, stateController.zoomedPaletteOffset, null)
    }

    private fun drawMagnifier(canvas: Canvas) {
        val centerPos = stateController.magnifierCenterPos
        if (centerPos == PaletteStateController.MAGNIFIER_UNINITIALIZED) {
            return
        }

        val magnifierHalfWidth = stateController.magnifierHalfWidth
        val offset = stateController.zoomedPaletteHorizontalMargin
        magnifierRect.right = centerPos + magnifierHalfWidth + offset
        magnifierRect.left = centerPos - magnifierHalfWidth + offset

        canvas.drawRoundRect(magnifierRect, zoomBorderRadius, zoomBorderRadius, magnifierPaint)
    }

    private fun drawZoomedPaletteContent() {
        val paletteLoops = stateController.loops
        val scaleX = stateController.paletteScaleX
        val horizontalMargin = stateController.paletteHorizontalMargin
        val zoomCellHeight = stateController.zoomedPaletteCellHeight
        val topCellYStart = stateController.zoomedPaletteInnerMargin
        val topCellYEnd = topCellYStart + zoomCellHeight
        val bottomCellYStart = topCellYEnd + topCellYStart
        val bottomCellYEnd = bottomCellYStart + zoomCellHeight
        val zoomVerticalOffset = stateController.zoomedPaletteVerticalMargin
        val zoomHorizontalOffset = stateController.zoomedPaletteHorizontalMargin
        val zoomBorderRect = initZoomedPaletteBorderRect(zoomHorizontalOffset, zoomVerticalOffset)
        val paletteTotalWidth = stateController.paletteTotalWidth

        zoomedPaletteCanvas.drawRoundRect(zoomBorderRect, zoomBorderRadius, zoomBorderRadius, paletteBorderPaint)

        val onlyLoopsWidthScaled = (paletteTotalWidth - 2 * horizontalMargin) * scaleX
        val offsetsInsideZoom = 2 * (horizontalMargin - zoomHorizontalOffset)
        val magnifierWidth = onlyLoopsWidthScaled + offsetsInsideZoom
        stateController.initializeMagnifierWidth(magnifierWidth)

        for (loop in paletteLoops) {
            val rawXStart = loop.virtualXStartPosition - horizontalMargin
            val rawXEnd = loop.virtualXEndPosition - horizontalMargin

            val xStart = rawXStart * scaleX + horizontalMargin
            val xEnd = rawXEnd * scaleX + horizontalMargin
            val yStart = if (loop.isUpper) topCellYStart else bottomCellYStart
            val yEnd = if (loop.isUpper) topCellYEnd else bottomCellYEnd

            loopDrawer.drawWithNoShader(zoomedPaletteCanvas, xStart, xEnd, yStart, yEnd, loop.model)
        }
    }

    private fun initLoopsShaderDrawer() {
        loopDrawer = LoopDrawer(sizesCalculator, loopShadersStorage)
    }

    private fun initZoomedPaletteBitmap(width: Int, height: Int) {
        if (::zoomedPaletteBitmap.isInitialized) {
            zoomedPaletteBitmap.recycle()
        }

        zoomedPaletteBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        zoomedPaletteCanvas = Canvas(zoomedPaletteBitmap)
    }

    private fun initMagnifierRect(zoomPaletteHeight: Float) {
        val zoomVerticalOffset = stateController.zoomedPaletteOffset
        val bottom = zoomVerticalOffset + zoomPaletteHeight
        magnifierRect.set(0F, zoomVerticalOffset, 0F, bottom)
    }

    private fun initZoomedPaletteBorderRect(horizontalOffset: Float, verticalOffset: Float): RectF {
        return RectF().apply {
            set(
                horizontalOffset,
                verticalOffset,
                zoomedPaletteCanvas.width.toFloat() - horizontalOffset,
                zoomedPaletteCanvas.height.toFloat() - verticalOffset
            )
        }
    }
}
