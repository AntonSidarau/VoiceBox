package com.jovvi.voicebox.android.feature.editor.di

import com.jovvi.voicebox.shared.common.AndroidSimpleLogger
import com.jovvi.voicebox.shared.common.ext.lazyUnsafe
import com.jovvi.voicebox.shared.feature.editor.AndroidEditComponentsSizeProvider
import com.jovvi.voicebox.shared.feature.editor.helper.EditorComponentsSizeProvider
import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator
import com.jovvi.voicebox.shared.feature.editor.ui.widget.field.FieldStateController
import com.jovvi.voicebox.shared.feature.editor.ui.widget.palette.PaletteStateController

interface EditorModule {

    val sizesCalculator: EditorSizesCalculator

    val fieldStateController: FieldStateController

    val paletteStateController: PaletteStateController

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

                override val paletteStateController: PaletteStateController
                    get() = PaletteStateController(sizesCalculator, AndroidSimpleLogger)
            }
        }
    }
}
