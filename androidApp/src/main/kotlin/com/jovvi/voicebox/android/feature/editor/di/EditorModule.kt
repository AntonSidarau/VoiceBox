package com.jovvi.voicebox.android.feature.editor.di

import com.jovvi.voicebox.android.feature.editor.ui.widget.field.LoopShadersStorage
import com.jovvi.voicebox.shared.business.editor.helper.LoopColorStorage
import com.jovvi.voicebox.shared.common.AndroidSimpleLogger
import com.jovvi.voicebox.shared.common.ext.lazyUnsafe
import com.jovvi.voicebox.shared.feature.editor.AndroidEditComponentsSizeProvider
import com.jovvi.voicebox.shared.feature.editor.helper.EditorComponentsSizeProvider
import com.jovvi.voicebox.shared.feature.editor.helper.EditorSizesCalculator
import com.jovvi.voicebox.shared.feature.editor.helper.SharedStateHolder
import com.jovvi.voicebox.shared.feature.editor.ui.widget.editor.EditorStateController
import com.jovvi.voicebox.shared.feature.editor.ui.widget.field.FieldStateController
import com.jovvi.voicebox.shared.feature.editor.ui.widget.palette.PaletteStateController

interface EditorModule {

    val sizesCalculator: EditorSizesCalculator

    val fieldStateController: FieldStateController

    val paletteStateController: PaletteStateController

    val editorStateController: EditorStateController

    val editorSharedStateHolder: SharedStateHolder

    val loopShadersStorage: LoopShadersStorage

    companion object {

        fun create(deps: EditorDependencies): EditorModule {
            return object : EditorModule {

                private val sizeProvider: EditorComponentsSizeProvider
                    get() = AndroidEditComponentsSizeProvider(deps.activityHolder)

                override val sizesCalculator: EditorSizesCalculator by lazyUnsafe {
                    EditorSizesCalculator(sizeProvider)
                }

                override val loopShadersStorage: LoopShadersStorage by lazyUnsafe {
                    LoopShadersStorage(LoopColorStorage)
                }

                override val editorSharedStateHolder: SharedStateHolder by lazyUnsafe {
                    SharedStateHolder()
                }

                override val fieldStateController: FieldStateController
                    get() = FieldStateController(editorSharedStateHolder, AndroidSimpleLogger)

                override val paletteStateController: PaletteStateController
                    get() = PaletteStateController(sizesCalculator, AndroidSimpleLogger)

                override val editorStateController: EditorStateController
                    get() = EditorStateController(sizesCalculator, editorSharedStateHolder)
            }
        }
    }
}
