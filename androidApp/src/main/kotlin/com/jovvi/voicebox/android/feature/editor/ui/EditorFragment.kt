package com.jovvi.voicebox.android.feature.editor.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.jovvi.voicebox.android.R
import com.jovvi.voicebox.android.common.di.component.ComponentContainer
import com.jovvi.voicebox.android.common.ui.BaseFragment
import com.jovvi.voicebox.android.feature.editor.di.EditorComponent
import dev.chrisbanes.insetter.applyInsetter

class EditorFragment : BaseFragment(R.layout.fragment_editor), ComponentContainer {

    override val componentClass: Class<*> get() = EditorComponent::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val editorLayout: ViewGroup = view.findViewById(R.id.view_editor)

        editorLayout.applyInsetter { type(statusBars = true, navigationBars = true) { padding() } }
    }
}
