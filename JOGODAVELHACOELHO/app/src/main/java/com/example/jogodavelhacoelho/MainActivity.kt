package com.example.jogodavelhacoelho

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import android.content.Intent
import android.media.MediaPlayer
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.jogodavelhacoelho.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var clickSound: MediaPlayer
    private lateinit var playerWinSound: MediaPlayer
    private lateinit var botWinSound: MediaPlayer
    private lateinit var drawSound: MediaPlayer


    var jogadorAtual = "X"
    val tabuleiro = arrayOf(
        arrayOf("", "", ""),
        arrayOf("", "", ""),
        arrayOf("", "", "")
    )
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)

        val musicServiceIntent = Intent(this, MusicService::class.java)
        startService(musicServiceIntent)
        playerWinSound = MediaPlayer.create(this, R.raw.playerganha)
        botWinSound = MediaPlayer.create(this, R.raw.botganha)
        drawSound = MediaPlayer.create(this, R.raw.empate)
        clickSound = MediaPlayer.create(this, R.raw.tucyoshi)
    }
    override fun onDestroy() {
        super.onDestroy()
        // Libera o MediaPlayer quando a atividade é destruída
        clickSound.release()
        playerWinSound.release()
        botWinSound.release()
        drawSound.release()
    }

    fun buttonClick(view: View) {
        val buttonSelecionado = view as Button

        if (buttonSelecionado.text.isNotEmpty()) {
            return
        }

        if (clickSound.isPlaying) {
            clickSound.stop()
            clickSound.prepare()
        }
        clickSound.start()

        buttonSelecionado.setBackgroundResource(R.drawable.marcacaox)
        buttonSelecionado.isEnabled = false

        when (buttonSelecionado.id) {
            binding.button1.id -> tabuleiro[0][0] = jogadorAtual
            binding.button2.id -> tabuleiro[0][1] = jogadorAtual
            binding.button3.id -> tabuleiro[0][2] = jogadorAtual
            binding.button4.id -> tabuleiro[1][0] = jogadorAtual
            binding.button5.id -> tabuleiro[1][1] = jogadorAtual
            binding.button6.id -> tabuleiro[1][2] = jogadorAtual
            binding.button7.id -> tabuleiro[2][0] = jogadorAtual
            binding.button8.id -> tabuleiro[2][1] = jogadorAtual
            binding.button9.id -> tabuleiro[2][2] = jogadorAtual
        }

        val vencedor = verificarVencedor(tabuleiro)
        if (vencedor != null) {
            when (vencedor) {
                "X" -> {
                    Toast.makeText(this, "Vencedor: Jogador", Toast.LENGTH_SHORT).show()
                    playSound(playerWinSound)
                }
                "O" -> {
                    Toast.makeText(this, "Vencedor: BOT", Toast.LENGTH_SHORT).show()
                    playSound(botWinSound)
                }
                "Empate" -> {
                    Toast.makeText(this, "Empate!", Toast.LENGTH_SHORT).show()
                    playSound(drawSound)
                }
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        jogadorAtual = if (jogadorAtual == "X") "O" else "X"
        if (jogadorAtual == "O") {
            jogarIA()
        }
    }

    private fun jogarIA() {
        val moves = mutableListOf<Pair<Int, Int>>()
        for (i in tabuleiro.indices) {
            for (j in tabuleiro[i].indices) {
                if (tabuleiro[i][j].isEmpty()) {
                    moves.add(Pair(i, j))
                }
            }
        }
        if (moves.isNotEmpty()) {
            val (i, j) = moves[Random.nextInt(moves.size)]
            tabuleiro[i][j] = "O"
            val button = getButtonAt(i, j)
            button?.setBackgroundResource(R.drawable.marcacaoo)
            button?.isEnabled = false

            val vencedor = verificarVencedor(tabuleiro)
            if (vencedor != null) {
                Toast.makeText(this, if (vencedor == "Empate") "Empate!" else "Vencedor: $vencedor", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return
            }

            jogadorAtual = "X"
        }
    }

    private fun getButtonAt(i: Int, j: Int): Button? {
        return when (Pair(i, j)) {
            Pair(0, 0) -> binding.button1
            Pair(0, 1) -> binding.button2
            Pair(0, 2) -> binding.button3
            Pair(1, 0) -> binding.button4
            Pair(1, 1) -> binding.button5
            Pair(1, 2) -> binding.button6
            Pair(2, 0) -> binding.button7
            Pair(2, 1) -> binding.button8
            Pair(2, 2) -> binding.button9
            else -> null
        }
    }

    fun verificarVencedor(tabuleiro: Array<Array<String>>): String? {
        for (i in 0 until 3) {
            if (tabuleiro[i][0].isNotEmpty() && tabuleiro[i][0] == tabuleiro[i][1] && tabuleiro[i][1] == tabuleiro[i][2]) {
                return tabuleiro[i][0]
            }
            if (tabuleiro[0][i].isNotEmpty() && tabuleiro[0][i] == tabuleiro[1][i] && tabuleiro[1][i] == tabuleiro[2][i]) {
                return tabuleiro[0][i]
            }
        }
        if (tabuleiro[0][0].isNotEmpty() && tabuleiro[0][0] == tabuleiro[1][1] && tabuleiro[1][1] == tabuleiro[2][2]) {
            return tabuleiro[0][0]
        }
        if (tabuleiro[0][2].isNotEmpty() && tabuleiro[0][2] == tabuleiro[1][1] && tabuleiro[1][1] == tabuleiro[2][0]) {
            return tabuleiro[0][2]
        }

        var empate = 0
        for (linha in tabuleiro) {
            for (valor in linha) {
                if (valor == "X" || valor == "O") {
                    empate++
                }
            }
        }

        return if (empate == 9) "Empate" else null
    }
    private fun playSound(mediaPlayer: MediaPlayer) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.prepare()
        }
        mediaPlayer.start()
    }
}

