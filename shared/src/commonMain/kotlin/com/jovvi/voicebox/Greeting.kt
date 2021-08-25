package com.jovvi.voicebox

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}