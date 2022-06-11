package com.jovvi.voicebox.shared.common.logger

actual object LoggerFactory {

    actual fun create(tag: String): SimpleLogger {
        return AndroidSimpleLogger(tag)
    }
}
