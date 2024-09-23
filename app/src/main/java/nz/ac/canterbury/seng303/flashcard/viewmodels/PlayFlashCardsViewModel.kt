package nz.ac.canterbury.seng303.flashcard.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.flashcard.datastore.Storage
import nz.ac.canterbury.seng303.flashcard.models.FlashCard

class PlayFlashCardsViewModel(private val flashCardStorage: Storage<FlashCard>) : ViewModel() {
    private val _flashCards = MutableStateFlow<List<FlashCard>>(emptyList())
    val flashCards: StateFlow<List<FlashCard>> get() = _flashCards

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> get() = _currentIndex

    private val _selectedAnswers = MutableStateFlow<MutableMap<Int, String>>(mutableMapOf())
    val selectedAnswers: StateFlow<MutableMap<Int, String>> get() = _selectedAnswers

    private val _correctAnswersCount = MutableStateFlow(0)
    val correctAnswersCount: StateFlow<Int> get() = _correctAnswersCount

    fun loadFlashCards() = viewModelScope.launch {
        flashCardStorage.getAll()
            .catch { e -> Log.e("PLAY_FLASH_CARDS_VIEW_MODEL", "Error loading flash cards", e) }
            .collect { flashCards ->
                // Display the flash cards and answers in a random order
                val shuffledFlashCards = flashCards.shuffled().map { flashCard ->
                    flashCard.copy(answers = flashCard.answers.shuffled())
                }
                _flashCards.value = shuffledFlashCards
                _currentIndex.value = 0
                _selectedAnswers.value.clear()
                _correctAnswersCount.value = 0
            }
    }

    fun selectAnswer(answer: String) {
        Log.d("PlayFlashCardsViewModel", "Selected answer: $answer")
        val updatedAnswers = _selectedAnswers.value.toMutableMap().apply {
            this[_currentIndex.value] = answer
        }
        _selectedAnswers.value = updatedAnswers
    }

    fun nextFlashCard() {
        if (_currentIndex.value < _flashCards.value.size - 1) {
            _currentIndex.value += 1
        }
    }

    fun evaluateResults() {
        val correctAnswers = _flashCards.value.filterIndexed { index, flashCard ->
            val selectedAnswer = _selectedAnswers.value[index]
            val selectedAnswerIndex = flashCard.answers.indexOf(selectedAnswer)
            flashCard.correctAnswerIndex == selectedAnswerIndex
        }.count()
        _correctAnswersCount.value = correctAnswers
    }
}