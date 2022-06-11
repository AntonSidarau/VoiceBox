package com.jovvi.voicebox.shared.common.logger

expect object LoggerFactory {

    fun create(tag: String = "SimpleLogger"): SimpleLogger
}
