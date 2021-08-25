package com.jovvi.voicebox.android.common.ui

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment(@LayoutRes layoutResId: Int) : Fragment(layoutResId) {

    open fun onBackPressed() {}
}
