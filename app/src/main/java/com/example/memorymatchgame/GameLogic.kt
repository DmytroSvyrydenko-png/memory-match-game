package com.example.memorymatchgame

// Šī klase ir atbildīga par spēles loģiku - spēles pārbaudi, skaitīšanu, gājienu pārslēgšanu un bota gājienu. Uzglabā vērtību un pašreizējo statusu.
class GameLogic(
    private val isPvP: Boolean,           // true - spēlētājs pret spēlētāju, false - pret datoru
    val p1Name: String,                   // pirmā spēlētāja vārds
    val p2Name: String                    // otra spēlētāja (vai "Computer")
) {
    // Saraksts ar augļu pāriem (2 no katra veida), kas nejauši sajaukti kopā.
    val cards: List<Card> = listOf(
        "Apple", "Pear", "Banana", "Grape",
        "Orange", "Lemon", "Mango", "Kiwi"
    ).flatMap { fruit -> listOf(Card(fruit), Card(fruit)) }
        .shuffled()

    private var currentPlayer = 0            // 0 - pirmais spēlētājs, 1 - otrais spēlētājs vai dators
    private var firstCardIndex: Int? = null  // pirmās atvērtās kartes indekss
    val scores = intArrayOf(0, 0)            // spēlētāju punkti

    // Flip funkcija tiek izsaukta pēc katra klikšķa uz kartes.
    fun flip(index: Int): Boolean? {
        val card = cards[index]

        // Nedrīkst atvērt karti, kas jau ir atvērta vai uzminēta.
        if (card.isFaceUp || card.isMatched) return null

        card.isFaceUp = true

        // Ja šī ir pirmā karte, vienkārši saglabājam to atmiņā
        if (firstCardIndex == null) {
            firstCardIndex = index
            return null
        }

        // Pārbaudīt, vai ir sakritība ar otro karti
        val firstCard = cards[firstCardIndex!!]
        val matched = firstCard.value == card.value

        if (matched) {
            // Ja tie sakrīt - jāatzīmē kā uzminēts
            firstCard.isMatched = true
            card.isMatched = true
            scores[currentPlayer]++
        }

        // Gatavošanās nākamajam gājienam
        firstCardIndex = null

        // Ja tie nesakrīt, gājiens tiek nodots nākamajam spēlētājam.
        if (!matched) currentPlayer = 1 - currentPlayer

        return matched
    }

    fun allMatched(): Boolean = cards.all { it.isMatched }

    fun currentPlayerName(): String = if (currentPlayer == 0) p1Name else p2Name

    fun isComputerTurn(): Boolean = !isPvP && currentPlayer == 1

    // Datora gājiens: izvēlas 2 nejauši atlasītas slēptās kartes.
    fun botMove(): Pair<Int, Int> {
        val hidden = cards.indices.filter { !cards[it].isFaceUp && !cards[it].isMatched }
        val first = hidden.random()
        val second = (hidden - first).random()
        return Pair(first, second)
    }
}
