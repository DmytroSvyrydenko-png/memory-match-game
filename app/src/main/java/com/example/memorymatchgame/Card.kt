package com.example.memorymatchgame

// Viena karte. Uzglabā vērtību un pašreizējo statusu.
data class Card(
    val value: String,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)
