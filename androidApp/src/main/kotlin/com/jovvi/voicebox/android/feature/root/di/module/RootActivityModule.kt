package com.jovvi.voicebox.android.feature.root.di.module

import com.jovvi.voicebox.android.app.di.AppComponent
import com.jovvi.voicebox.shared.common.ui.ActivityHolder

interface RootActivityModule {

    val activityHolder: ActivityHolder

    companion object {

        fun create(deps: AppComponent): RootActivityModule {
            return object : RootActivityModule {

                override val activityHolder: ActivityHolder = ActivityHolder()
            }
        }
    }
}
