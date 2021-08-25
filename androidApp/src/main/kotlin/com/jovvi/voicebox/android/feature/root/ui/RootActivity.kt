package com.jovvi.voicebox.android.feature.root.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.jovvi.voicebox.android.R
import com.jovvi.voicebox.android.common.di.Injector.getComponent
import com.jovvi.voicebox.android.common.di.component.ComponentContainer
import com.jovvi.voicebox.android.common.ui.BaseFragment
import com.jovvi.voicebox.android.feature.editor.ui.EditorFragment
import com.jovvi.voicebox.android.feature.root.di.RootComponent

class RootActivity : AppCompatActivity(), ComponentContainer {

    private val currentFragment: BaseFragment?
        get() = supportFragmentManager.findFragmentById(R.id.root_container) as? BaseFragment

    override val componentClass: Class<*> get() = RootComponent::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies()
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.root_container, EditorFragment())
                .commit()
        }

        initUi()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // currentFragment?.onBackPressed() ?: super.onBackPressed()
    }

    private fun injectDependencies() {
        val component: RootComponent = getComponent()
        component.activityHolder.updateActivity(this)
    }

    private fun initUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}
