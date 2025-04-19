package com.example.memorymatchgame

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Interfeisa elementi
        val inputPlayer1 = findViewById<EditText>(R.id.etPlayer1)
        val inputPlayer2 = findViewById<EditText>(R.id.etPlayer2)
        val modeSelector = findViewById<RadioGroup>(R.id.rgMode)
        val startButton  = findViewById<Button>(R.id.btnStart)

        // Pārslēgt spēles režīmu (PvP vai PvC)
        modeSelector.setOnCheckedChangeListener { _, selectedId ->
            // Aktivizēt otrā spēlētāja lauku tikai tad, ja ir atlasīts PvP režīms.
            inputPlayer2.isEnabled = (selectedId == R.id.rbPvp)
        }

        // Pogas “Start Game” nospiešanas apstrāde
        startButton.setOnClickListener {
            // Iegūst pirmā spēlētāja vārdu
            val player1Name = inputPlayer1.text.toString().ifBlank { "Player 1" }

            // Atkarībā no režīma otrs spēlētājs būs vai nu spēlētājs, vai dators.
            val isPvP = modeSelector.checkedRadioButtonId == R.id.rbPvp
            val player2Name = if (isPvP) {
                inputPlayer2.text.toString().ifBlank { "Player 2" }
            } else {
                "Computer"
            }

            // Sākt GameActivity un pārsūtīt datus
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("P1", player1Name)
            intent.putExtra("P2", player2Name)
            intent.putExtra("PVP", isPvP)
            startActivity(intent)
        }
    }
}
