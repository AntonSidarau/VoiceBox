package com.jovvi.voicebox.android.common.di.component

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.jovvi.voicebox.android.common.di.Injector

class FragmentComponentCleaner : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        if (f is ComponentContainer && !f.requireActivity().isChangingConfigurations) {
            Injector.remove(f)
        }
    }
}
