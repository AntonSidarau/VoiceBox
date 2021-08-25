package com.jovvi.voicebox.shared.common.ui.ext

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.use

fun Context.dpToPx(dp: Float): Float {
    return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.spToPx(sp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
}

fun Context.getThemeColor(@AttrRes themeAttrRes: Int): Int {
    return obtainStyledAttributes(intArrayOf(themeAttrRes)).use {
        it.getColor(0, Color.MAGENTA)
    }
}

fun Context.getFont(@FontRes fontRes: Int): Typeface {
    return ResourcesCompat.getFont(this, fontRes)!!
}
