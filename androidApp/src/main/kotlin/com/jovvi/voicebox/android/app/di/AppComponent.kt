package com.jovvi.voicebox.android.app.di

import android.app.Application
import com.jovvi.voicebox.android.app.di.module.AndroidAppModule

interface AppComponent :
    AndroidAppModule {

    companion object {

        fun create(application: Application): AppComponent {
            val appModule = AndroidAppModule.create(application)
            return object : AppComponent,
                AndroidAppModule by appModule {}
        }
    }
}
