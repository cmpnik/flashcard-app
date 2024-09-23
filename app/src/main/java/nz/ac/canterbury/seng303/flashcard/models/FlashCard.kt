package nz.ac.canterbury.seng303.flashcard.models

data class FlashCard(
    val id: Int,
    val question: String,
    val answers: List<String>,
    val correctAnswerIndex: Int): Identifiable {

    companion object {
        fun getFlashCards(): List<FlashCard> {
            // Default flashcards
            return listOf(
                FlashCard(
                    1,
                    "What is the capital of New Zealand",
                    listOf("Auckland", "Wellington", "Christchurch", "Dunedin"),
                    1
                ),
                FlashCard(
                    2,
                    "What year was the treaty of Waitangi drafted?",
                    listOf("1820", "1830", "1840", "1850", "1860"),
                    2
                ),
                FlashCard(
                    3,
                    "What is the te reo name for New Zealand?",
                    listOf("Aotearoa", "Middle Earth", "Akarana"),
                    0
                )
            )
        }
    }

    override fun getIdentifier(): Int {
        return id
    }
}

