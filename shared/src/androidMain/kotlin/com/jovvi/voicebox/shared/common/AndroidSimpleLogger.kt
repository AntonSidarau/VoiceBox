package com.jovvi.voicebox.shared.common

import android.util.Log

object AndroidSimpleLogger : SimpleLogger {

    override fun logDebug(message: String) {
        Log.d("SimpleLogger", message)
    }
}
