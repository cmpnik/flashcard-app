package nz.ac.canterbury.seng303.flashcard.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CreateFlashCardViewModel : ViewModel() {
    var question by mutableStateOf("")
        private set

    fun updateQuestion(newQuestion: String) {
        question = newQuestion
    }

    var answers by mutableStateOf<List<String>>(emptyList())
        private set

    fun updateAnswers(newAnswers: List<String>) {
        answers = newAnswers
    }

    var correctAnswerIndex by mutableStateOf(-1)
        private set

    fun updateCorrectAnswerIndex(newIndex: Int) {
        correctAnswerIndex = newIndex
    }
}
