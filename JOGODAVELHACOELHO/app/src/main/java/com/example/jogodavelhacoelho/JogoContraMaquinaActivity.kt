package com.example.jogodavelhacoelho

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.jogodavelhacoelho.databinding.ActivityJogoContraMaquinaBinding
import kotlin.random.Random

class JogoContraMaquinaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJogoContraMaquinaBinding
    private lateinit var somClique: MediaPlayer
    private lateinit var somJogadorGanha: MediaPlayer
    private lateinit var somBotGanha: MediaPlayer
    private lateinit var somEmpate: MediaPlayer

    // Variável que indica o jogador atual
    var jogadorAtual = "X"
    // Matriz que representa o tabuleiro do jogo
    val tabuleiro = arrayOf(
        arrayOf("", "", ""),
        arrayOf("", "", ""),
        arrayOf("", "", "")
    )
    // Variável para armazenar o nível de dificuldade
    private var nivelDificuldade: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJogoContraMaquinaBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)

        // Recebe o nível de dificuldade da tela inicial
        nivelDificuldade = intent.getStringExtra("nivel_dificuldade")

        // Inicializa os sons do jogo
        somJogadorGanha = MediaPlayer.create(this, R.raw.playerganha)
        somBotGanha = MediaPlayer.create(this, R.raw.botganha)
        somEmpate = MediaPlayer.create(this, R.raw.empate)
        somClique = MediaPlayer.create(this, R.raw.tucyoshi)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Libera os recursos do MediaPlayer quando a atividade é destruída
        somClique.release()
        somJogadorGanha.release()
        somBotGanha.release()
        somEmpate.release()
    }

    // Função chamada quando um botão é clicado
    fun buttonClick(view: View) {
        val botaoSelecionado = view as Button

        // Verifica se o botão já foi clicado anteriormente
        if (botaoSelecionado.text.isNotEmpty()) {
            return
        }

        // Toca o som de clique
        if (somClique.isPlaying) {
            somClique.stop()
            somClique.prepare()
        }
        somClique.start()

        // Define a marcação "X" no botão clicado
        botaoSelecionado.setBackgroundResource(R.drawable.marcacaox)
        botaoSelecionado.isEnabled = false

        // Atualiza o tabuleiro com a jogada do jogador
        when (botaoSelecionado.id) {
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

        // Verifica se há um vencedor e faz a jogada da IA se não houver
        val vencedor = verificarVencedor(tabuleiro)
        if (vencedor != null) {
            // Exibe uma mensagem com o resultado
            mostrarResultado(vencedor)
        } else {
            jogadorAtual = if (jogadorAtual == "X") "O" else "X"
            if (jogadorAtual == "O") {
                jogarIA()
            }
        }
    }

    // Função que decide a jogada da IA com base no nível de dificuldade
    private fun jogarIA() {
        if (nivelDificuldade == "facil") {
            jogarIAFacil()
        } else {
            jogarIADificil()
        }
    }

    // IA para nível fácil: escolhe uma posição aleatória
    private fun jogarIAFacil() {
        val movimentosPossiveis = mutableListOf<Pair<Int, Int>>()
        for (i in tabuleiro.indices) {
            for (j in tabuleiro[i].indices) {
                if (tabuleiro[i][j].isEmpty()) {
                    movimentosPossiveis.add(Pair(i, j))
                }
            }
        }
        if (movimentosPossiveis.isNotEmpty()) {
            val (i, j) = movimentosPossiveis[Random.nextInt(movimentosPossiveis.size)]
            tabuleiro[i][j] = "O"
            val botao = getButtonAt(i, j)
            botao?.setBackgroundResource(R.drawable.marcacaoo)
            botao?.isEnabled = false

            val vencedor = verificarVencedor(tabuleiro)
            if (vencedor != null) {
                mostrarResultado(vencedor)
            } else {
                jogadorAtual = "X"
            }
        }
    }

    // IA para nível difícil: tenta vencer ou bloquear o jogador
    private fun jogarIADificil() {
        // 1. Tenta encontrar uma jogada vencedora para a IA
        for (i in tabuleiro.indices) {
            for (j in tabuleiro[i].indices) {
                if (tabuleiro[i][j].isEmpty()) {
                    tabuleiro[i][j] = "O"
                    if (verificarVencedor(tabuleiro) == "O") {
                        // Se a jogada leva à vitória, realiza a jogada
                        val botao = getButtonAt(i, j)
                        botao?.setBackgroundResource(R.drawable.marcacaoo)
                        botao?.isEnabled = false
                        mostrarResultado("O")
                        return
                    }
                    tabuleiro[i][j] = "" // Desfaz a jogada
                }
            }
        }

        // 2. Tenta bloquear a jogada vencedora do jogador
        for (i in tabuleiro.indices) {
            for (j in tabuleiro[i].indices) {
                if (tabuleiro[i][j].isEmpty()) {
                    tabuleiro[i][j] = "X"
                    if (verificarVencedor(tabuleiro) == "X") {
                        // Se o jogador está prestes a vencer, bloqueia a jogada
                        tabuleiro[i][j] = "O"
                        val botao = getButtonAt(i, j)
                        botao?.setBackgroundResource(R.drawable.marcacaoo)
                        botao?.isEnabled = false
                        jogadorAtual = "X"
                        return
                    }
                    tabuleiro[i][j] = "" // Desfaz a jogada
                }
            }
        }

        // 3. Faz uma jogada aleatória se não encontrou jogada vencedora ou bloqueio
        jogarIAFacil()
    }

    // Função que retorna o botão na posição especificada pelo índice da matriz
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

    // Função que verifica se há um vencedor
    fun verificarVencedor(tabuleiro: Array<Array<String>>): String? {
        // Verifica linhas e colunas
        for (i in 0 until 3) {
            if (tabuleiro[i][0].isNotEmpty() && tabuleiro[i][0] == tabuleiro[i][1] && tabuleiro[i][1] == tabuleiro[i][2]) {
                return tabuleiro[i][0]
            }
            if (tabuleiro[0][i].isNotEmpty() && tabuleiro[0][i] == tabuleiro[1][i] && tabuleiro[1][i] == tabuleiro[2][i]) {
                return tabuleiro[0][i]
            }
        }
        // Verifica diagonais
        if (tabuleiro[0][0].isNotEmpty() && tabuleiro[0][0] == tabuleiro[1][1] && tabuleiro[1][1] == tabuleiro[2][2]) {
            return tabuleiro[0][0]
        }
        if (tabuleiro[0][2].isNotEmpty() && tabuleiro[0][2] == tabuleiro[1][1] && tabuleiro[1][1] == tabuleiro[2][0]) {
            return tabuleiro[0][2]
        }

        // Verifica se houve empate
        var empate = 0
        for (linha in tabuleiro) {
            for (valor in linha) {
                if (valor == "X" || valor == "O") {
                    empate++
                }
            }
        }

        // Retorna "Empate" se todas as posições estiverem preenchidas sem um vencedor
        return if (empate == 9) "Empate" else null
    }

    // Função que mostra o resultado do jogo e reinicia
    private fun mostrarResultado(vencedor: String) {
        val mensagem = when (vencedor) {
            "X" -> "Vencedor: Jogador"
            "O" -> "Vencedor: Máquina"
            else -> "Empate!"
        }
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
        // Reinicia o jogo
        val intent = Intent(this, NovoJogoActivity::class.java)
        startActivity(intent)
        finish()
    }
}