package com.jovvi.voicebox.android.feature.editor.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.jovvi.voicebox.android.R
import com.jovvi.voicebox.android.common.di.component.ComponentContainer
import com.jovvi.voicebox.android.common.ui.BaseFragment
import com.jovvi.voicebox.android.feature.editor.di.EditorComponent
import com.jovvi.voicebox.android.feature.editor.ui.widget.EditorLayout
import com.jovvi.voicebox.shared.business.editor.helper.LoopColorStorage
import com.jovvi.voicebox.shared.business.editor.model.Loop
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EditorFragment : BaseFragment(R.layout.fragment_editor), ComponentContainer {

    override val componentClass: Class<*> get() = EditorComponent::class.java

    private lateinit var editorLayout: EditorLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        editorLayout = view.findViewById(R.id.view_editor)

        editorLayout.applyInsetter { type(statusBars = true, navigationBars = true) { padding() } }

        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000L)

            val list = mutableListOf<Loop>()
            repeat(20) {
                list.add(Loop("$it", LoopColorStorage.provide(it), Loop.Size.FOUR))
            }
            editorLayout.paletteView.prePopulate(list)
        }

        bindViewActions(editorLayout)
    }

    override fun onDestroyView() {
        editorLayout.clearListeners()
        super.onDestroyView()
    }

    private fun bindViewActions(editorLayout: EditorLayout) {
        with(editorLayout) {
            paletteView.setOnStartDraggingLoopListener { xPos, yPos, loop ->
                editorLayout.startDragging(xPos, yPos, loop)
            }
            paletteView.setOnEndDraggingLoopListener { editorLayout.stopDragging() }
            paletteView.setOnDraggingLoopListener { distanceX, distanceY ->
                editorLayout.updateDraggingLoopPositions(distanceX, distanceY)
            }
        }
    }
}
