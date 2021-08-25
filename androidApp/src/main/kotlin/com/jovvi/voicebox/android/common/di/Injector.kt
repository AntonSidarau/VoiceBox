package com.jovvi.voicebox.android.common.di

import android.app.Application
import com.jovvi.voicebox.android.common.di.component.ActivityComponentCleaner
import com.jovvi.voicebox.android.common.di.component.ComponentContainer
import com.jovvi.voicebox.android.common.di.component.LazyRemovableComponent

object Injector {

    private val lazyReferenceStore: MutableMap<Class<*>, LazyRemovableComponent<*>> = linkedMapOf()

    fun init(
        application: Application,
        lazyRemovableComponents: List<LazyRemovableComponent<*>>
    ) {
        for (component in lazyRemovableComponents) {
            lazyReferenceStore[component.componentClass] = component
        }
        application.registerActivityLifecycleCallbacks(ActivityComponentCleaner())
    }

    fun remove(container: ComponentContainer) {
        lazyReferenceStore[container.componentClass]?.onRemove()
    }

    fun <T> getComponent(componentClass: Class<*>): T {
        return this[componentClass]
    }

    fun <T> ComponentContainer.getComponent(): T {
        return getComponent(this.componentClass)
    }

    private operator fun <T> get(componentClass: Class<*>): T {
        val component = lazyReferenceStore[componentClass]
            ?: throw IllegalStateException(
                "Provide LazyRemovableComponent for ${componentClass.name} in InjectorInitializer"
            )

        @Suppress("UNCHECKED_CAST")
        return component.get() as T
    }
}
