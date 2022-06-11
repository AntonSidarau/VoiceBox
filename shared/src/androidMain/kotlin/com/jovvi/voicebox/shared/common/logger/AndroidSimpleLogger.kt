package com.jovvi.voicebox.shared.common.logger

import android.util.Log

class AndroidSimpleLogger(private val tag: String) : SimpleLogger {

    override fun logDebug(message: String) {
        Log.d(tag, message)
    }

    override fun logError(e: Throwable, message: String) {
        Log.e(tag, message, e)
    }
}
