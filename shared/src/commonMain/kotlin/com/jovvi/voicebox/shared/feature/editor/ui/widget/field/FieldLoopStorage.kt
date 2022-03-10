package com.jovvi.voicebox.shared.feature.editor.ui.widget.field

import com.jovvi.voicebox.shared.business.editor.model.Loop
import com.jovvi.voicebox.shared.feature.editor.EditorSettings

class FieldLoopStorage(
    private val cellWidth: Float,
    private val cellHeight: Float,
    private val cellMargin: Float
) {

    private val store: MutableMap<Int, FieldLoop> = hashMapOf()
    private val keyStore: MutableList<Int> = mutableListOf()

    private var extractedLoop: Pair<Int, FieldLoop>? = null

    val extractedFieldLoop: FieldLoop? get() = extractedLoop?.second

    val lastLoop: FieldLoop? get() = keyStore.lastOrNull()?.let { store[it] }

    val availableLoops: Collection<FieldLoop> get() = store.values

    fun put(rowNumber: Int, columnNumber: Int, loop: Loop, loopWidth: Float) {
        if (rowNumber > EditorSettings.FIELD_CONTENT_CELLS_COUNT - 1 ||
            columnNumber < 0 ||
            columnNumber > EditorSettings.TIMELINE_LENGTH_COLUMNS
        ) {
            tryRestoreExtractedLoop()
            return
        }

        val key = calculateKey(rowNumber, columnNumber)
        val loopStartXPos = calculateVirtualXPos(columnNumber, cellWidth, cellMargin)
        val loopYPos = calculateVirtualYPos(rowNumber, cellHeight, cellMargin)

        val fieldLoop = FieldLoop(
            loopStartXPos,
            loopStartXPos + loopWidth,
            loopYPos,
            loop,
            columnNumber,
            rowNumber
        )

        extractedLoop = null
        addLoopToStorage(fieldLoop, key)
    }

    fun remove(key: Int) {
        extractedLoop = null
        removeLoopFromStorage(key)
    }

    fun getLoopByKey(key: Int): FieldLoop? {
        return store[key]
    }

    fun isAllowedToPutLoop(rowNumber: Int, columnNumber: Int, loopSize: Loop.Size): Boolean {
        var result = true
        for (i in 0 until loopSize.value) {
            val leftmostKey = columnNumber - i
            if (store[calculateKey(rowNumber, leftmostKey)] != null) {
                result = false
                break
            }

            val rightmostKey = columnNumber + i
            if (store[calculateKey(rowNumber, rightmostKey)] != null) {
                result = false
                break
            }
        }

        return result
    }

    fun tryRestoreExtractedLoop() {
        val item = extractedLoop
        if (item != null) {
            val key = item.first
            addLoopToStorage(item.second, key)
        }
    }

    fun clear() {
        store.clear()
        keyStore.clear()
        extractedLoop = null
    }

    fun calculateKey(rowNumber: Int, columnNumber: Int): Int {
        val keyBase = columnNumber * EditorSettings.FIELD_CONTENT_CELLS_COUNT
        return keyBase + rowNumber
    }

    private fun calculateVirtualXPos(
        keyOffset: Int,
        cellWidth: Float,
        cellMargin: Float
    ): Float {
        return keyOffset * (cellWidth + cellMargin) + cellMargin
    }

    private fun calculateVirtualYPos(
        keyOffset: Int,
        cellHeight: Float,
        cellMargin: Float
    ): Float {
        return (keyOffset + 1) * (cellHeight + cellMargin)
    }

    private fun addLoopToStorage(loop: FieldLoop, key: Int) {
        store[key] = loop
        addKeyToStore(key)
    }

    private fun removeLoopFromStorage(key: Int) {
        store.remove(key)
        keyStore.remove(key)
    }

    private fun addKeyToStore(key: Int) {
        keyStore.add(key)
        keyStore.sort()
    }
}
