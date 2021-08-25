package com.jovvi.voicebox.android.app.di.module

import android.app.Application
import android.content.Context

interface AndroidAppModule {

    val appContext: Context

    val application: Application

    companion object {

        fun create(application: Application): AndroidAppModule {
            return object : AndroidAppModule {

                override val appContext: Context = application

                override val application: Application = application
            }
        }
    }
}
