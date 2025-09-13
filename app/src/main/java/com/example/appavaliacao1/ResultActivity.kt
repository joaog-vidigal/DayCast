package com.example.appavaliacao1

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ResultActivity : AppCompatActivity() {

    private val apiKey = "40716f8f92262832e56f953083e9d90b"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val cidade = intent.getStringExtra("cidade") ?: return

        buscarClima(cidade)

        // Data e hora atual formatadas
        val timezone = TimeZone.getTimeZone("America/Sao_Paulo")
        val locale = Locale("pt", "BR")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", locale)
        val timeFormat = SimpleDateFormat("HH:mm", locale)
        dateFormat.timeZone = timezone
        timeFormat.timeZone = timezone

        val currentDate = dateFormat.format(Date())
        val currentTime = timeFormat.format(Date())

        findViewById<TextView>(R.id.txtData).text = currentDate
        findViewById<TextView>(R.id.txtHora).text = currentTime
    }

    fun voltar(view: View) {
        finish()
    }

    private fun buscarClima(cidade: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = URL("https://api.openweathermap.org/data/2.5/weather?q=$cidade&appid=$apiKey&units=metric&lang=pt_br").readText()
                val json = JSONObject(response)

                val main = json.getJSONObject("main")
                val temp = main.getDouble("temp")
                val feelsLike = main.getDouble("feels_like")
                val pressure = main.getInt("pressure")
                val humidity = main.getInt("humidity")

                val weather = json.getJSONArray("weather").getJSONObject(0)
                val description = weather.getString("description")
                var iconCode = weather.getString("icon") // "01d", "03n", etc.
                if (!iconCode.startsWith("01")) iconCode = iconCode.slice(0..1)
                val iconName = "icon_$iconCode"

                val wind = json.getJSONObject("wind")
                val windSpeed = wind.getDouble("speed") * 3.6 // m/s para km/h

                val name = json.getString("name")
                val txtTemperatura = findViewById<TextView>(R.id.txtTemperatura)
                // Coloração do indicador de temperatura
                when {
                    temp <= 0 -> txtTemperatura.setTextColor(Color.parseColor("#431AD9"))
                    temp <= 11 -> txtTemperatura.setTextColor(Color.parseColor("#1A53D9"))
                    temp <= 19 -> txtTemperatura.setTextColor(Color.parseColor("#1CCEFF"))
                    temp <= 27 -> txtTemperatura.setTextColor(Color.parseColor("#D9C91A"))
                    temp <= 32 -> txtTemperatura.setTextColor(Color.parseColor("#FF7B1C"))
                    else -> txtTemperatura.setTextColor(Color.parseColor("#FF331C"))
                }

                withContext(Dispatchers.Main) {
                    // Atualiza o ícone central
                    val resId = resources.getIdentifier(iconName, "drawable", packageName)
                    val imageView = findViewById<ImageView>(R.id.imageView4)
                    if (resId != 0) {
                        imageView.setImageResource(resId)
                    }

                    // Atualiza os textos da UI
                    findViewById<TextView>(R.id.txtCidade).text = name
                    txtTemperatura.text = "${"%.1f".format(temp)}°C"
                    findViewById<TextView>(R.id.txtSensacao).text = "${"%.1f".format(feelsLike)}°C"
                    findViewById<TextView>(R.id.txtPressao).text = "$pressure hPa"
                    findViewById<TextView>(R.id.txtDescricao).text = "$description"
                    findViewById<TextView>(R.id.txtUmidade).text = "$humidity%"
                    findViewById<TextView>(R.id.txtVento).text = "${windSpeed.toInt()} km/h"
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.txtCidade).text = "Erro ao buscar dados."
                }
            }
        }
    }
}
