package com.solverai.gtoapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        val tv = findViewById<TextView>(R.id.helpText)
        tv.text = """Cómo usar el solver

Modo principiante (predeterminado):
- Selecciona el rango (A,K,Q,J,T,9...2) y el palo (D,T,C,P) y presiona + para añadir la carta.
- Repite para completar 2, 5, 6 o 7 cartas según quieras (preflop, flop, turn, river).

Modo rápido:
- Escribe las cartas en formato compacto, por ejemplo: ADKC7T8P9C
  - A = As, D = Diamante, K = Rey, C = Corazon, etc.
- Las cartas deben ingresarse en pares (valor+palo).

Configuración:
- Cambia entre formato Español (D,T,C,P) y Inglés (d,c,h,s) en Configuración.

Notas:
- No se permiten cartas duplicadas.
- Para probar, asegúrate de que el servidor en http://20.83.146.164:5000 esté activo y la API key sea correcta (por defecto la app trae 08152601.servidor).
"""
    }
}
