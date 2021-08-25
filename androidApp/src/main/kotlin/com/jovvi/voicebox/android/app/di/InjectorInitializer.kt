package com.jovvi.voicebox.android.app.di

import android.app.Application
import com.jovvi.voicebox.android.common.di.Injector
import com.jovvi.voicebox.android.common.di.component.LazyRemovableComponent
import com.jovvi.voicebox.android.feature.editor.di.EditorComponent
import com.jovvi.voicebox.android.feature.root.di.RootComponent

object InjectorInitializer {

    fun init(application: Application) {
        AppInjector.newInstance(AppComponent.create(application))
        Injector.init(
            application, listOf(
                LazyRemovableComponent(RootComponent::class.java) {
                    RootComponent.create()
                },
                LazyRemovableComponent(EditorComponent::class.java) {
                    getComponent<RootComponent>().createEditorComponent()
                }
            )
        )
    }

    private inline fun <reified T> InjectorInitializer.getComponent(): T {
        return Injector.getComponent(T::class.java)
    }
}
