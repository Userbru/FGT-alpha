package com.fgt.macroime.logic

import android.view.KeyEvent

// Macro = lista de (KEYCODE, atrasoMs)
typealias Macro = List<Pair<Int, Long>>

object MacroStore {
    private val table = mutableMapOf<String, Macro>()

    fun record(trigger: String, seq: Macro) { table[trigger] = seq }
    fun find(trigger: String): Macro? = table[trigger]

    // Exemplo inicial para testar: START+Y envia K, K, J
    fun exampleLoad() {
        record(
            "KEYCODE_BUTTON_START+KEYCODE_BUTTON_Y",
            listOf(
                KeyEvent.KEYCODE_K to 0L,
                KeyEvent.KEYCODE_K to 60L,
                KeyEvent.KEYCODE_J to 40L
            )
        )
    }
}
