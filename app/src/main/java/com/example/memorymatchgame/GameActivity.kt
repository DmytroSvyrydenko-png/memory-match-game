package com.example.memorymatchgame

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private lateinit var game: GameLogic                 // Galvenā spēles loģika
    private val uiCards = mutableListOf<Button>()        // Saraksts ar visām kartiņu pogām (UI)
    private val delay = Handler(Looper.getMainLooper())  // Handler priekš pauzēm (piemēram, pirms kartes aizveras)
    private var isBusy = false                           // Kad true – bloķējam klikus, kamēr darbojas animācija

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Nosaukumu un režīma iegūšana no MainActivity
        val player1 = intent.getStringExtra("P1") ?: "Player 1"
        val player2 = intent.getStringExtra("P2") ?: "Player 2"
        val isPvP = intent.getBooleanExtra("PVP", true)

        // Inicializējam spēles loģiku
        game = GameLogic(isPvP, player1, player2)

        // Sveiciena teksts augšpusē
        findViewById<TextView>(R.id.tvGreeting).text =
            "Hello, $player1 and $player2! Can you find all the pairs?"

        // Pogas "Menu" un "Restart"
        findViewById<Button>(R.id.btnMenu).setOnClickListener { finish() } // atpakaļ uz sākumu
        findViewById<Button>(R.id.btnRestart).setOnClickListener {
            finish()
            startActivity(intent) // restartēt spēli
        }

        createCardGrid() // izveidojam 16 kartes
        updateUI()       // parāda sākotnējo stāvokli

        // Ja spēli sāk dators
        if (game.isComputerTurn()) botPlay()
    }

    // Izveidot 4×4 pogu režģi
    private fun createCardGrid() {
        val grid = findViewById<GridLayout>(R.id.grid)
        grid.rowCount = 4
        grid.columnCount = 4

        // Katru karti attēlojam ar pogu ar tekstu "?"
        game.cards.forEachIndexed { index, _ ->
            val button = Button(this).apply {
                text = "?"
                setOnClickListener { onCardClick(index) } // ko darīt klikšķa gadījumā
            }
            uiCards.add(button)
            grid.addView(button)
        }
    }

    // Kartiņas nospiešana
    private fun onCardClick(index: Int) {
        if (isBusy || game.isComputerTurn()) return // neļaujam klikot, ja dators dara savu gājienu


        val matched = game.flip(index) ?: run {
            updateUI()
            return
        }

        updateUI()

        // Ja visas kārtis atrastas – parādām rezultātu
        if (matched) {
            if (game.allMatched()) showResultDialog()
        } else {
            isBusy = true
            // 1 sekundi parādām divas kartes, tad aizveram
            delay.postDelayed({
                hideUnmatchedCards()
                updateUI()
                isBusy = false

                if (game.isComputerTurn()) botPlay() // ja dators ir nākamais – ļaujam viņam spēlēt
            }, 1000)
        }
    }

    // Bota gājiens
    private fun botPlay() {
        isBusy = true
        updateUI()

        val (first, second) = game.botMove() // izvēlamies divas nejaušas kartes

        game.flip(first); updateUI()

        delay.postDelayed({
            game.flip(second); updateUI()

            if (game.allMatched()) {
                showResultDialog()
            } else if (!game.isComputerTurn()) {
                delay.postDelayed({
                    hideUnmatchedCards()
                    updateUI()
                    isBusy = false
                }, 800)
            } else {
                delay.postDelayed({
                    isBusy = false
                    botPlay()
                }, 800)
            }
        }, 700)
    }

    // Paslēpt visas neatminētās kartes
    private fun hideUnmatchedCards() {
        game.cards.forEach {
            if (!it.isMatched) it.isFaceUp = false
        }
    }

    // Atjaunināt UI: teksts uz pogām, rezultāts, kura kārta ir pienākusi.
    private fun updateUI() {
        game.cards.forEachIndexed { i, card ->
            uiCards[i].text = if (card.isFaceUp || card.isMatched) card.value else "?"
            uiCards[i].isEnabled = !card.isMatched
        }

        findViewById<TextView>(R.id.tvTurn).text = "Move: ${game.currentPlayerName()}"
        findViewById<TextView>(R.id.tvScore1).text = "${game.p1Name}: ${game.scores[0]}"
        findViewById<TextView>(R.id.tvScore2).text = "${game.p2Name}: ${game.scores[1]}"
    }

    // Parādīt logu ar rezultātu
    private fun showResultDialog() {
        val (score1, score2) = game.scores
        val message = when {
            score1 > score2 -> "${game.p1Name} won and ${game.p2Name} lost! $score1 vs $score2"
            score2 > score1 -> "${game.p2Name} wins and ${game.p1Name} lost! $score2 vs $score1"
            else -> "Draw! $score1 : $score2"
        }

        AlertDialog.Builder(this)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Play again") { _, _ -> finish() }
            .setNegativeButton("Exit") { _, _ -> finishAffinity() }
            .show()
    }
}
