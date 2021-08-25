package com.jovvi.voicebox.android.common.di.component

class LazyRemovableComponent<T : Any>(
    internal val componentClass: Class<T>,
    private val provideComponent: () -> T
) : RemovableComponent<T> {

    private var storedComponent: T? = null

    override fun get(): T {
        val component = storedComponent
        if (component == null) {
            storedComponent = provideComponent()
        }

        return storedComponent!!
    }

    override fun onRemove() {
        storedComponent = null
    }
}
