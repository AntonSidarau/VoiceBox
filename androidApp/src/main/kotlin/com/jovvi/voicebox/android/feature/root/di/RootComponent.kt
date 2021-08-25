package com.jovvi.voicebox.android.feature.root.di

import com.jovvi.voicebox.android.app.di.AppComponent
import com.jovvi.voicebox.android.app.di.getAppComponent
import com.jovvi.voicebox.android.app.di.module.AndroidAppModule
import com.jovvi.voicebox.android.feature.editor.di.EditorComponent
import com.jovvi.voicebox.android.feature.editor.di.EditorDependencies
import com.jovvi.voicebox.android.feature.root.di.module.RootActivityModule

interface RootComponent :
    AndroidAppModule,
    RootActivityModule,

    EditorDependencies {

    companion object {

        fun create(): RootComponent {
            val appComponent = getAppComponent()

            val rootActivityModule = RootActivityModule.create(appComponent)

            return object : RootComponent,
                AppComponent by appComponent,
                RootActivityModule by rootActivityModule {}
        }
    }

    fun createEditorComponent(): EditorComponent {
        return EditorComponent.create(this)
    }
}
