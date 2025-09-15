package com.solverai.gtoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val API_URL = "http://20.83.146.164:5000/solver"
    private val DEFAULT_API_KEY = "08152601.servidor"

    private lateinit var modeSwitch: Switch
    private lateinit var quickInput: EditText
    private lateinit var btnResolve: Button
    private lateinit var apiKeyInput: EditText
    private lateinit var resultView: TextView
    private lateinit var rankSpinner: Spinner
    private lateinit var suitSpinner: Spinner
    private lateinit var addCardBtn: Button
    private lateinit var cardsList: RecyclerView
    private lateinit var cardsAdapter: CardsAdapter
    private val cardEntries = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        modeSwitch = findViewById(R.id.modeSwitch)
        quickInput = findViewById(R.id.quickInput)
        btnResolve = findViewById(R.id.btnResolve)
        apiKeyInput = findViewById(R.id.apiKeyInput)
        resultView = findViewById(R.id.resultView)
        rankSpinner = findViewById(R.id.rankSpinner)
        suitSpinner = findViewById(R.id.suitSpinner)
        addCardBtn = findViewById(R.id.addCardBtn)
        cardsList = findViewById(R.id.cardsList)

        apiKeyInput.setText(DEFAULT_API_KEY)

        val ranks = arrayOf("A","K","Q","J","T","9","8","7","6","5","4","3","2")
        val spanishSuits = arrayOf("D","T","C","P")
        val adapterR = ArrayAdapter(this, android.R.layout.simple_spinner_item, ranks)
        adapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rankSpinner.adapter = adapterR
        val adapterS = ArrayAdapter(this, android.R.layout.simple_spinner_item, spanishSuits)
        adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        suitSpinner.adapter = adapterS

        cardsAdapter = CardsAdapter(cardEntries) { pos ->
            cardEntries.removeAt(pos); cardsAdapter.notifyDataSetChanged()
        }
        cardsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        cardsList.adapter = cardsAdapter

        modeSwitch.isChecked = false
        setModeUI(false)

        modeSwitch.setOnCheckedChangeListener { _, isChecked -> setModeUI(isChecked) }

        addCardBtn.setOnClickListener {
            val rank = rankSpinner.selectedItem.toString()
            val suit = suitSpinner.selectedItem.toString()
            val code = "${rank}${suit}"
            if (cardEntries.contains(code)) {
                Toast.makeText(this, "Carta duplicada", Toast.LENGTH_SHORT).show()
            } else {
                cardEntries.add(code)
                cardsAdapter.notifyDataSetChanged()
            }
        }

        findViewById<Button>(R.id.btnConfig).setOnClickListener {
            startActivity(Intent(this, ConfigActivity::class.java))
        }
        findViewById<Button>(R.id.btnHelp).setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }

        btnResolve.setOnClickListener {
            val apiKey = apiKeyInput.text.toString().trim()
            if (apiKey.isEmpty()) {
                Toast.makeText(this, "Ingrese API Key", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val cardsListToSend = if (modeSwitch.isChecked) {
                parseQuickInput(quickInput.text.toString())
            } else {
                cardEntries.toList()
            }
            if (cardsListToSend == null) {
                showAlert("Formato inválido", "Verifique las cartas ingresadas.")
                return@setOnClickListener
            }
            val payload = JSONObject()
            payload.put("api_key", apiKey)
            when (cardsListToSend.size) {
                2 -> {
                    payload.put("hero", listOf(cardsListToSend[0], cardsListToSend[1]))
                }
                5,6,7 -> {
                    payload.put("hero", listOf(cardsListToSend[0], cardsListToSend[1]))
                    val board = cardsListToSend.subList(2, cardsListToSend.size)
                    payload.put("board", board)
                }
                else -> {
                    showAlert("Formato inválido", "Se esperan 2, 5, 6 o 7 cartas.")
                    return@setOnClickListener
                }
            }
            resultView.text = "Enviando..."
            sendRequest(payload.toString(), apiKey)
        }
    }

    private fun setModeUI(quickMode: Boolean) {
        if (quickMode) {
            quickInput.visibility = View.VISIBLE
            rankSpinner.visibility = View.GONE
            suitSpinner.visibility = View.GONE
            addCardBtn.visibility = View.GONE
            cardsList.visibility = View.GONE
        } else {
            quickInput.visibility = View.GONE
            rankSpinner.visibility = View.VISIBLE
            suitSpinner.visibility = View.VISIBLE
            addCardBtn.visibility = View.VISIBLE
            cardsList.visibility = View.VISIBLE
        }
    }

    private fun showAlert(title: String, msg: String) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun parseQuickInput(input: String): List<String>? {
        val cleaned = input.replace("\\s+".toRegex(), "").uppercase()
        if (cleaned.length % 2 != 0) return null
        val result = mutableListOf<String>()
        val validRanks = setOf("A","K","Q","J","T","9","8","7","6","5","4","3","2")
        val validSuits = setOf("D","T","C","P")
        for (i in cleaned.indices step 2) {
            val r = cleaned[i].toString()
            val s = cleaned[i+1].toString()
            if (r !in validRanks) return null
            if (s !in validSuits) return null
            val code = "${r}${s}"
            if (result.contains(code)) return null
            result.add(code)
        }
        return result
    }

    private fun sendRequest(jsonPayload: String, apiKey: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BASIC
                val client = OkHttpClient.Builder().addInterceptor(logging).build()
                val body = jsonPayload.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val req = Request.Builder()
                    .url(API_URL)
                    .addHeader("x-api-key", apiKey)
                    .post(body)
                    .build()
                val resp = client.newCall(req).execute()
                val text = resp.body?.string() ?: "[empty]"
                runOnUiThread { resultView.text = text }
            } catch (e: Exception) {
                runOnUiThread { resultView.text = "Error: ${e.message}" }
            }
        }
    }
}

