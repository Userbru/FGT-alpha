package com.fgt.macroime

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fgt.macroime.logic.MacroStore

class MainActivity : AppCompatActivity() {
  private val recording = mutableListOf<Pair<Int, Long>>()
  private var last = 0L

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    MacroStore.exampleLoad()

    val btnRecord: Button = findViewById(R.id.btn_record)
    val btnSave: Button = findViewById(R.id.btn_save)
    val tvCount: TextView = findViewById(R.id.tv_count)

    btnRecord.setOnClickListener { recording.clear(); last = now(); tvCount.text = "Eventos: 0" }
    btnSave.setOnClickListener {
      MacroStore.record("KEYCODE_BUTTON_START+KEYCODE_BUTTON_Y", recording.toList())
      recording.clear(); tvCount.text = "Eventos: 0"
    }
  }

  override fun dispatchKeyEvent(event: KeyEvent): Boolean {
    if (event.action == KeyEvent.ACTION_DOWN) {
      val dt = now() - last; last = now()
      recording.add(event.keyCode to dt.coerceAtLeast(0))
      findViewById<TextView>(R.id.tv_count).text = "Eventos: ${recording.size}"
      return true
    }
    return super.dispatchKeyEvent(event)
  }
  private fun now() = System.currentTimeMillis()
}
