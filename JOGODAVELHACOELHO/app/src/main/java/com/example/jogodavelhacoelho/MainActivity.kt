package com.example.jogodavelhacoelho

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jogodavelhacoelho.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura o clique dos botões
        binding.botaoPvmFacil.setOnClickListener {
            // Abre a tela do jogo contra a máquina no nível fácil
            val intent = Intent(this, JogoContraMaquinaActivity::class.java)
            intent.putExtra("nivel_dificuldade", "facil")
            startActivity(intent)
        }

        binding.botaoPvmDificil.setOnClickListener {
            // Abre a tela do jogo contra a máquina no nível difícil
            val intent = Intent(this, JogoContraMaquinaActivity::class.java)
            intent.putExtra("nivel_dificuldade", "dificil")
            startActivity(intent)
        }
    }
}
