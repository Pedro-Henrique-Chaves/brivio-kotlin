package com.example.jogodavelhacoelho

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jogodavelhacoelho.databinding.ActivityNovoJogoBinding

class NovoJogoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNovoJogoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovoJogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura o clique do botão para iniciar um novo jogo
        binding.botaoNovoJogo.setOnClickListener {
            val intent = Intent(this, JogoContraMaquinaActivity::class.java)
            startActivity(intent)
            finish()
        }
       // Configura o clique do botão para voltar para o menu principal
        binding.menuprincipal.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
