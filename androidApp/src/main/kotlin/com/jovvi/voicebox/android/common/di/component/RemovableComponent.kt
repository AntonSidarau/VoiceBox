package com.jovvi.voicebox.android.common.di.component

interface RemovableComponent<T> {

    fun get(): T

    fun onRemove()
}
