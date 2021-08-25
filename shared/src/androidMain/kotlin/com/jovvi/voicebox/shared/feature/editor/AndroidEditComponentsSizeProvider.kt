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
private const val TITLE_BAR_HEIGHT_DP = 100F
private const val CONTROLS_BAR_HEIGHT_DP = 90F
private const val FIXED_PALETTE_BAR_HEIGHT_DP = 56F
private const val PALETTE_MARGIN_BOTTOM_DP = 12F
private const val PALETTE_MARGIN_TOP_DP = 8F

@Suppress("DEPRECATION")
class AndroidEditComponentsSizeProvider(
    activityHolder: ActivityHolder
) : EditorComponentsSizeProvider {

    private val width: Float

    init {
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

    override val totalWidth: Float = width

    override val cellMargin: Float = activityHolder.activity.dpToPx(CELL_MARGIN_DP)

    override val cellCornerRadius: Float = activityHolder.activity.dpToPx(CELL_CORNER_RADIUS_DP)

    override val titleBarHeight: Float = activityHolder.activity.dpToPx(TITLE_BAR_HEIGHT_DP)

    override val controlsBarHeight: Float = activityHolder.activity.dpToPx(CONTROLS_BAR_HEIGHT_DP)

    override val paletteZoomHeight: Float = activityHolder.activity.dpToPx(
        FIXED_PALETTE_BAR_HEIGHT_DP + 2 * PALETTE_MARGIN_BOTTOM_DP + PALETTE_MARGIN_TOP_DP
    )

    override val paletteBottomMargin: Float = activityHolder.activity.dpToPx(PALETTE_MARGIN_BOTTOM_DP)

    override val paletteTopMargin: Float = activityHolder.activity.dpToPx(PALETTE_MARGIN_TOP_DP)
}
