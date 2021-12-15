package com.jovvi.voicebox.shared.feature.editor

import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.content.getSystemService
import com.jovvi.voicebox.shared.common.ui.ActivityHolder
import com.jovvi.voicebox.shared.common.ui.ext.dpToPx
import com.jovvi.voicebox.shared.feature.editor.helper.EditorComponentsSizeProvider

private const val CELL_MARGIN_DP = 3F
private const val CELL_CORNER_RADIUS_DP = 6F
private const val TITLE_BAR_HEIGHT_DP = 96F
private const val CONTROLS_BAR_HEIGHT_DP = 86F
private const val FIXED_PALETTE_BAR_HEIGHT_DP = 56F
private const val PALETTE_INNER_VERTICAL_MARGIN_DP = 12F
private const val PALETTE_OUTER_VERTICAL_MARGIN_DP = 8F
private const val PALETTE_HORIZONTAL_MARGIN_DP = 6F
private const val PALETTE_ZOOM_BORDER_WIDTH_DP = 2F

@Suppress("DEPRECATION")
class AndroidEditComponentsSizeProvider(
    activityHolder: ActivityHolder
) : EditorComponentsSizeProvider {

    private val width: Float

    init {
        // TODO think about activity holder
        val activity = activityHolder.activity
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.display?.getRealMetrics(displayMetrics)
        } else {
            val windowManager: WindowManager = activity.getSystemService()!!
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        }

        width = displayMetrics.widthPixels.toFloat()
    }

    override val totalWidth: Float get() = width

    override val cellMargin: Float = activityHolder.activity.dpToPx(CELL_MARGIN_DP)

    override val cellCornerRadius: Float = activityHolder.activity.dpToPx(CELL_CORNER_RADIUS_DP)

    override val titleBarHeight: Float = activityHolder.activity.dpToPx(TITLE_BAR_HEIGHT_DP)

    override val controlsBarHeight: Float = activityHolder.activity.dpToPx(CONTROLS_BAR_HEIGHT_DP)

    override val paletteZoomHeight: Float = activityHolder.activity.dpToPx(FIXED_PALETTE_BAR_HEIGHT_DP)

    override val paletteInnerVerticalMargin: Float = activityHolder.activity.dpToPx(PALETTE_INNER_VERTICAL_MARGIN_DP)

    override val paletteOuterVerticalMargin: Float = activityHolder.activity.dpToPx(PALETTE_OUTER_VERTICAL_MARGIN_DP)

    override val paletteHorizontalMargin: Float = activityHolder.activity.dpToPx(PALETTE_HORIZONTAL_MARGIN_DP)

    override val zoomedPaletteBorderWidth: Float = activityHolder.activity.dpToPx(PALETTE_ZOOM_BORDER_WIDTH_DP)
}
