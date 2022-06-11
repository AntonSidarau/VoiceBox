package com.jovvi.voicebox.shared.common.logger

interface SimpleLogger {

    fun logDebug(message: String)

    fun logError(e: Throwable, message: String)
}

