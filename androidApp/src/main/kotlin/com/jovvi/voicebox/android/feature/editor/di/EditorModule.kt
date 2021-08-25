package com.jovvi.voicebox.android.feature.editor.di

import com.jovvi.voicebox.shared.common.ext.lazyUnsafe
import com.jovvi.voicebox.shared.feature.editor.AndroidEditComponentsSizeProvider
import com.jovvi.voicebox.shared.feature.editor.helper.EditorComponentsSizeProvider
import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator
import com.jovvi.voicebox.shared.feature.editor.ui.widget.field.FieldStateController

interface EditorModule {

    val sizesCalculator: EditorSizesCalculator

    val fieldStateController: FieldStateController

    companion object {

        fun create(deps: EditorDependencies): EditorModule {
            return object : EditorModule {

                private val sizeProvider: EditorComponentsSizeProvider
                    get() = AndroidEditComponentsSizeProvider(deps.activityHolder)

                override val sizesCalculator: EditorSizesCalculator by lazyUnsafe {
                    EditorSizesCalculator(sizeProvider)
                }

                override val fieldStateController: FieldStateController
                    get() = FieldStateController()
            }
        }
    }
}
