package com.jovvi.voicebox.shared.business.editor.helper

import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.common.ext.lazyUnsafe
import com.jovvi.voicebox.shared.common.model.ColorMpp

object LoopColorStorage {

    private val colors: List<Loop.Color> by lazyUnsafe {
        listOf(
            Loop.Color( // light blue
                ColorMpp(red = 0x4a, green = 0xda, blue = 0xfe),
                ColorMpp(red = 0x7a, green = 0xe6, blue = 0xfe)
            ),
            Loop.Color( // light purple
                ColorMpp(red = 0xbb, green = 0xa1, blue = 0xff),
                ColorMpp(red = 0xcf, green = 0xbd, blue = 0xff)
            ),
            Loop.Color( // blue
                ColorMpp(red = 0x66, green = 0xb8, blue = 0xff),
                ColorMpp(red = 0x91, green = 0xcd, blue = 0xff)
            ),
            Loop.Color( // light red
                ColorMpp(red = 0xfd, green = 0x7b, blue = 0x9c),
                ColorMpp(red = 0xfd, green = 0xa1, blue = 0xba)
            ),
            Loop.Color( // light green
                ColorMpp(red = 0x5a, green = 0xf6, blue = 0xc1),
                ColorMpp(red = 0x92, green = 0xf4, blue = 0xdc)
            ),
            Loop.Color( // green
                ColorMpp(red = 0x9e, green = 0xed, blue = 0x68),
                ColorMpp(red = 0xba, green = 0xed, blue = 0xa0)
            ),
            Loop.Color( // pink
                ColorMpp(red = 0xeb, green = 0x7c, blue = 0xd1),
                ColorMpp(red = 0xde, green = 0x98, blue = 0xd1)
            )
        )
    }

    fun provide(index: Int): Loop.Color {
        return colors[index % colors.size]
    }

    fun provideColors(): List<Loop.Color> = colors
}
