package com.fgt.macroime

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fgt.macroime.logic.MacroStore

class MainActivity : ComponentActivity() {
    private val recording = mutableStateListOf<Pair<Int, Long>>()
    private var last = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MacroStore.exampleLoad()
        setContent {
            var trigger by remember { mutableStateOf("KEYCODE_BUTTON_START+KEYCODE_BUTTON_Y") }
            Surface {
                Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("FGT Macro Keyboard", style = MaterialTheme.typography.titleLarge)
                    Text("1) Ative o teclado. 2) Ative a acessibilidade (filtrar teclas).")
                    OutlinedTextField(value = trigger, onValueChange = { trigger = it }, label = { Text("Gatilho (ex: START+Y)") })
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { recording.clear(); last = now() }) { Text("Gravar macro") }
                        Button(onClick = { MacroStore.record(trigger, recording.toList()); recording.clear() }) { Text("Salvar") }
                    }
                    Text("Eventos gravados: ${recording.size}")
                }
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            val dt = now() - last; last = now()
            recording += (event.keyCode to dt.coerceAtLeast(0))
            return true
        }
        return super.dispatchKeyEvent(event)
    }
    private fun now() = System.currentTimeMillis()
}
