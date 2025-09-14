package com.solverai.gtoapp

import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class ConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        val rg = findViewById<RadioGroup>(R.id.radioGroup)
        val spanish = findViewById<RadioButton>(R.id.rbSpanish)
        val english = findViewById<RadioButton>(R.id.rbEnglish)
        spanish.isChecked = true
    }
}
