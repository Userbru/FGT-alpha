package com.fgt.macroime.ime

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.inputmethodservice.InputMethodService

class FgtImeService : InputMethodService() {

    companion object {
        private var live: FgtImeService? = null

        fun sendKey(code: Int, actionDownThenUp: Boolean = true) {
            live?.sendKeyInternal(code, actionDownThenUp)
        }

        // Macro = lista de (KEYCODE, atrasoMs)
        fun sendSequence(seq: List<Pair<Int, Long>>) {
            live?.sendSequenceInternal(seq)
        }
    }

    private var ic: InputConnection? = null

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        live = this
        ic = currentInputConnection
    }

    override fun onFinishInput() {
        super.onFinishInput()
        if (live === this) live = null
    }

    private fun sendKeyInternal(code: Int, actionDownThenUp: Boolean) {
        val c = ic ?: return
        if (actionDownThenUp) {
            c.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, code))
            c.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, code))
        } else {
            c.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, code))
        }
    }

    private fun sendSequenceInternal(seq: List<Pair<Int, Long>>) {
        val c = ic ?: return
        for ((code, delay) in seq) {
            c.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, code))
            c.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, code))
            if (delay > 0) try { Thread.sleep(delay) } catch (_: InterruptedException) {}
        }
    }
}
