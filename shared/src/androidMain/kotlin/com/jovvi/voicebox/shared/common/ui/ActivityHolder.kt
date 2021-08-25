package com.jovvi.voicebox.shared.common.ui

import androidx.fragment.app.FragmentActivity

class ActivityHolder {

    private var internal: FragmentActivity? = null

    val activity get() = internal!!

    fun updateActivity(activity: FragmentActivity) {
        internal = activity
    }
}
