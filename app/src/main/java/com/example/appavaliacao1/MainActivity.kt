package com.example.appavaliacao1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    fun pesquisar(view: View) {
        val editTextCidade = findViewById<EditText>(R.id.edtxtPesquisa)
        val cidade = editTextCidade.text.toString().trim()

        if (cidade.isNotEmpty()) {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("cidade", cidade)
            startActivity(intent)
        }
    }
}
