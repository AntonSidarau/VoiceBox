package com.jovvi.voicebox.android.feature.editor.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.jovvi.voicebox.android.common.di.Injector
import com.jovvi.voicebox.android.feature.editor.di.EditorComponent
import com.jovvi.voicebox.android.feature.editor.ui.widget.field.EditorFieldView
import com.jovvi.voicebox.android.feature.editor.ui.widget.field.LoopDrawer
import com.jovvi.voicebox.android.feature.editor.ui.widget.field.LoopShadersStorage
import com.jovvi.voicebox.android.feature.editor.ui.widget.palette.EditorPaletteView
import com.jovvi.voicebox.android.feature.editor.ui.widget.title.EditorTitleView
import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator
import com.jovvi.voicebox.shared.feature.editor.ui.widget.editor.EditorStateController

private const val CHILDREN_COUNT = 4
private const val TITLE_BAR_INDEX = 0
private const val FIELD_INDEX = 1
private const val CONTROLS_BAR_INDEX = 2
private const val PALETTE_INDEX = 3

class EditorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val sizesCalculator: EditorSizesCalculator
    private val stateController: EditorStateController
    private val loopShadersStorage: LoopShadersStorage

    private lateinit var loopDrawer: LoopDrawer

    val fieldView: EditorFieldView
    val paletteView: EditorPaletteView
    val titleBar: EditorTitleView

    init {
        val component: EditorComponent = Injector.getComponent(EditorComponent::class.java)
        sizesCalculator = component.sizesCalculator
        stateController = component.editorStateController
        loopShadersStorage = component.loopShadersStorage

        setWillNotDraw(false)

        fieldView = EditorFieldView(context).apply { layoutParams = createDefaultChildLayoutParams() }
        titleBar = EditorTitleView(context).apply {
            layoutParams = createDefaultChildLayoutParams()
            onDeleteAllListener = { fieldView.clearLoops() }
        }
        val controlsBar = View(context).apply { layoutParams = createDefaultChildLayoutParams() }
        paletteView = EditorPaletteView(context).apply { layoutParams = createDefaultChildLayoutParams() }

        addView(titleBar)
        addView(fieldView)
        addView(controlsBar)
        addView(paletteView)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val addedChildren = childCount
        if (addedChildren != CHILDREN_COUNT) {
            throw IllegalArgumentException("Don't add any children to this layout")
        }

        var prevChildBottom = paddingTop
        for (i in 0 until addedChildren) {
            val child = getChildAt(i)
            val childHeight = child.measuredHeight
            child.layout(0, prevChildBottom, child.measuredWidth, prevChildBottom + childHeight)
            prevChildBottom += childHeight
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val containerWidth = MeasureSpec.getSize(widthMeasureSpec)
        val containerHeight = MeasureSpec.getSize(heightMeasureSpec)
        val availableHeight = containerHeight - (paddingTop + paddingBottom)

        sizesCalculator.calculate(availableHeight)

        measureTitleBar(getChildAt(TITLE_BAR_INDEX), widthMeasureSpec)
        measureField(getChildAt(FIELD_INDEX), widthMeasureSpec)
        measureControlsBar(getChildAt(CONTROLS_BAR_INDEX), widthMeasureSpec)
        measurePalette(getChildAt(PALETTE_INDEX), widthMeasureSpec)

        setMeasuredDimension(containerWidth, containerHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        stateController.initialize(w.toFloat(), h.toFloat())

        initLoopShadersStorage()
        initLoopDrawer()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        val loop = stateController.draggingLoop
        if (loop != null) {
            val xPos = stateController.draggingXPos
            val yPos = stateController.draggingYPos
            loopDrawer.draw(canvas, xPos, yPos, loop)
        }
    }

    fun startDragging(xPos: Float, yPos: Float, loop: Loop) {
        stateController.startDragging(xPos, yPos + paletteView.y, loop)
        invalidate()
    }

    fun updateDraggingLoopPositions(deltaX: Float, deltaY: Float) {
        stateController.updateDraggingPositions(deltaX, deltaY)
        invalidate()
    }

    fun stopDragging() {
        val isInField = stateController.isDraggingLoopInBounds(
            0F, fieldView.top.toFloat(), width.toFloat(), fieldView.bottom.toFloat()
        )
        val draggingLoop = stateController.draggingLoop
        if (draggingLoop != null && isInField) {
            val xPos = stateController.draggingXPos
            val yPos = stateController.draggingYPos - fieldView.top.toFloat()
            fieldView.tryAddLoop(xPos, yPos, draggingLoop)
        }

        stateController.stopDragging()
        invalidate()
    }

    fun clearListeners() {
        paletteView.clearListeners()
        titleBar.clearListeners()
    }

    private fun createDefaultChildLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
    }

    private fun measureTitleBar(titleBar: View, containerWidthMeasureSpec: Int) {
        val titleBarHeight = sizesCalculator.titleBarHeight.toInt()
        val heightMeasureSpec = MeasureSpec.makeMeasureSpec(titleBarHeight, MeasureSpec.EXACTLY)
        titleBar.measure(containerWidthMeasureSpec, heightMeasureSpec)
    }

    private fun measureField(field: View, containerWidthMeasureSpec: Int) {
        val fieldHeight = sizesCalculator.fieldHeight.toInt()
        val heightMeasureSpec = MeasureSpec.makeMeasureSpec(fieldHeight, MeasureSpec.EXACTLY)
        field.measure(containerWidthMeasureSpec, heightMeasureSpec)
    }

    private fun measureControlsBar(controlsBar: View, containerWidthMeasureSpec: Int) {
        val controlsBarHeight = sizesCalculator.controlsBarHeight.toInt()
        val heightMeasureSpec = MeasureSpec.makeMeasureSpec(controlsBarHeight, MeasureSpec.EXACTLY)
        controlsBar.measure(containerWidthMeasureSpec, heightMeasureSpec)
    }

    private fun measurePalette(palette: View, containerWidthMeasureSpec: Int) {
        val paletteHeight = sizesCalculator.paletteHeight.toInt()
        val heightMeasureSpec = MeasureSpec.makeMeasureSpec(paletteHeight, MeasureSpec.EXACTLY)
        palette.measure(containerWidthMeasureSpec, heightMeasureSpec)
    }

    private fun initLoopShadersStorage() {
        loopShadersStorage.initialize()
    }

    private fun initLoopDrawer() {
        loopDrawer = LoopDrawer(sizesCalculator, loopShadersStorage)
    }
}
