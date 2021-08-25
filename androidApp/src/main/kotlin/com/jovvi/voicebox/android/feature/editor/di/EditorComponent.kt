package com.jovvi.voicebox.android.feature.editor.di

interface EditorComponent : EditorDependencies, EditorModule {

    companion object {

        fun create(dependencies: EditorDependencies): EditorComponent {
            val module = EditorModule.create(dependencies)
            return object : EditorComponent,
                EditorModule by module,
                EditorDependencies by dependencies {}
        }
    }
}
