package com.fgt.macroime.access

import android.accessibilityservice.AccessibilityService
import android.view.KeyEvent
import com.fgt.macroime.ime.FgtImeService
import com.fgt.macroime.logic.MacroStore

class GamepadAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() { /* ok */ }

    // Permite filtrar teclas de hardware (controle)
    override fun onKeyEvent(event: KeyEvent): Boolean {
        val isGamepad = event.device?.sources?.let {
            (it and android.view.InputDevice.SOURCE_GAMEPAD) == android.view.InputDevice.SOURCE_GAMEPAD ||
            (it and android.view.InputDevice.SOURCE_JOYSTICK) == android.view.InputDevice.SOURCE_JOYSTICK
        } ?: false
        if (!isGamepad) return false

        if (event.action == KeyEvent.ACTION_DOWN) {
            // 1) Mapa simples botão→tecla (funciona como teclado)
            KeyMap.map(event.keyCode)?.let { mapped ->
                FgtImeService.sendKey(mapped)
                return true
            }
            // 2) Macro por "gatilho" (ex.: START+Y)
            val trigger = TriggerBuilder.feed(event.keyCode)
            MacroStore.find(trigger)?.let { macro ->
                FgtImeService.sendSequence(macro)
                return true
            }
        }
        return false
    }

    override fun onAccessibilityEvent(e: android.view.accessibility.AccessibilityEvent?) {}
    override fun onInterrupt() {}
}

/** Mapa inicial: ajuste como preferir */
object KeyMap {
    private val map = mapOf(
        KeyEvent.KEYCODE_BUTTON_A to KeyEvent.KEYCODE_J,
        KeyEvent.KEYCODE_BUTTON_B to KeyEvent.KEYCODE_L,
        KeyEvent.KEYCODE_BUTTON_X to KeyEvent.KEYCODE_U,
        KeyEvent.KEYCODE_BUTTON_Y to KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_BUTTON_START to KeyEvent.KEYCODE_ENTER
    )
    fun map(gamepadKey: Int) = map[gamepadKey]
}

/** Constrói gatilho com chord de 2 teclas (ex.: START+Y) */
object TriggerBuilder {
    private var last: Int? = null
    private var lastTime = 0L
    fun feed(code: Int): String {
        val now = System.currentTimeMillis()
        val res = if (last != null && now - lastTime < 600) {
            "${KeyEvent.keyCodeToString(last!!)}+${KeyEvent.keyCodeToString(code)}"
        } else {
            KeyEvent.keyCodeToString(code)
        }
        last = code; lastTime = now
        return res
    }
}
