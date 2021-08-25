package com.jovvi.voicebox.android.app

import android.app.Application
import com.jovvi.voicebox.android.app.di.InjectorInitializer

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        InjectorInitializer.init(this)
    }
}
