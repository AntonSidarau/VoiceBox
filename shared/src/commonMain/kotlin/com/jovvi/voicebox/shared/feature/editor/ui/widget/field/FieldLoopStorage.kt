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
    private val keyBalancer: HashMap<Int, Int> = hashMapOf()

    private var takenLoop: Pair<Int, FieldLoop>? = null

    var onLoopAdded: ((row: Int, column: Int, model: Loop) -> Unit)? = null
    var onLoopRemoved: ((row: Int, column: Int) -> Unit)? = null
    var onAllLoopsRemoved: (() -> Unit)? = null
    var onLoopsCountUpdated: ((Int) -> Unit)? = null

    val takenFieldLoop: FieldLoop? get() = takenLoop?.second
    val lastLoop: FieldLoop? get() = keyStore.lastOrNull()?.let { store[it] }
    val availableLoops: Collection<FieldLoop> get() = store.values

    fun put(rowNumber: Int, columnNumber: Int, loop: Loop, loopWidth: Float) {
        if (rowNumber > EditorSettings.FIELD_CONTENT_CELLS_COUNT - 1 ||
            columnNumber < 0 ||
            columnNumber > EditorSettings.TIMELINE_LENGTH_COLUMNS
        ) {
            tryReturnTakenLoop()
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

        takenLoop = null
        addLoopToStorage(fieldLoop, key, rowNumber, columnNumber)

        onLoopAdded?.invoke(rowNumber, columnNumber, loop)
        onLoopsCountUpdated?.invoke(store.size)
    }

    fun remove(rowNumber: Int, columnNumber: Int) {
        takenLoop = null
        val key = calculateKey(rowNumber, columnNumber)
        removeLoopFromStorage(key, rowNumber, columnNumber)

        onLoopRemoved?.invoke(rowNumber, columnNumber)
        onLoopsCountUpdated?.invoke(store.size)
    }

    fun getLoopBy(rowNumber: Int, columnNumber: Int): FieldLoop? {
        return store[calculateKey(rowNumber, columnNumber)]
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

    fun take(rowNumber: Int, columnNumber: Int): FieldLoop? {
        val key = calculateKey(rowNumber, columnNumber)
        val loopKey = keyBalancer[key]
        return loopKey?.let { takeLoopInternal(loopKey) }
    }

    fun tryReturnTakenLoop() {
        val item = takenLoop
        if (item != null) {
            val key = item.first
            addLoopToStorage(item.second, key, item.second.rowNumber, item.second.columnNumber)

            with(item.second) {
                onLoopAdded?.invoke(rowNumber, columnNumber, model)
                onLoopsCountUpdated?.invoke(store.size)
            }
        }
    }

    fun clear() {
        store.clear()
        keyStore.clear()
        keyBalancer.clear()
        takenLoop = null

        onAllLoopsRemoved?.invoke()
        onLoopsCountUpdated?.invoke(store.size)
    }

    private fun calculateKey(rowNumber: Int, columnNumber: Int): Int {
        val keyBase = columnNumber * EditorSettings.FIELD_CONTENT_CELLS_COUNT
        return keyBase + rowNumber
    }

    private fun takeLoopInternal(key: Int): FieldLoop? {
        val loop = store[key]
        if (loop != null) {
            takenLoop = key to loop
            val rowNumber = loop.rowNumber
            val columnNumber = loop.columnNumber
            removeLoopFromStorage(key, rowNumber, columnNumber)

            onLoopRemoved?.invoke(rowNumber, columnNumber)
        }

        return loop
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

    private fun addLoopToStorage(loop: FieldLoop, key: Int, rowNumber: Int, columnNumber: Int) {
        store[key] = loop
        addKeyToStore(key)

        repeat(loop.model.size.value) {
            val balancerKey = calculateKey(rowNumber, columnNumber + it)
            keyBalancer[balancerKey] = key
        }
    }

    private fun removeLoopFromStorage(key: Int, rowNumber: Int, columnNumber: Int) {
        val loop = store.remove(key)
        keyStore.remove(key)

        if (loop == null) return
        repeat(loop.model.size.value) {
            val balancerKey = calculateKey(rowNumber, columnNumber + it)
            keyBalancer.remove(balancerKey)
        }
    }

    private fun addKeyToStore(key: Int) {
        keyStore.add(key)
        keyStore.sort()
    }
}
