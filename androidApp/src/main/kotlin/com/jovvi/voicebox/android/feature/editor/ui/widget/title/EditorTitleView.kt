package com.jovvi.voicebox.android.feature.editor.ui.widget.title

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.FrameLayout
import com.jovvi.voicebox.android.R

class EditorTitleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var onDeleteAllListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_editor_title, this)

        val btnDeleteAll: Button = findViewById(R.id.btn_delete_all)
        btnDeleteAll.setOnClickListener { onDeleteAllListener?.invoke() }
    }

    fun clearListeners() {
        onDeleteAllListener = null
    }
}
