package com.jovvi.voicebox.android.feature.editor.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.jovvi.voicebox.android.common.di.Injector
import com.jovvi.voicebox.android.feature.editor.di.EditorComponent
import com.jovvi.voicebox.android.feature.editor.ui.widget.field.EditorFieldView
import com.jovvi.voicebox.android.feature.editor.ui.widget.palette.EditorPaletteView
import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator

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

    init {
        val component: EditorComponent = Injector.getComponent(EditorComponent::class.java)
        sizesCalculator = component.sizesCalculator

        setWillNotDraw(false)

        val titleBar = View(context).apply { layoutParams = createDefaultChildLayoutParams() }
        val field = EditorFieldView(context).apply { layoutParams = createDefaultChildLayoutParams() }
        val controlsBar = View(context).apply { layoutParams = createDefaultChildLayoutParams() }
        val palette = EditorPaletteView(context).apply { layoutParams = createDefaultChildLayoutParams() }

        addView(titleBar)
        addView(field)
        addView(controlsBar)
        addView(palette)
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
}
