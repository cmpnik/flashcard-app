package nz.ac.canterbury.seng303.flashcard.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.flashcard.datastore.Storage
import kotlin.random.Random
import kotlinx.coroutines.flow.collect
import nz.ac.canterbury.seng303.flashcard.models.FlashCard

class FlashCardViewModel(private val flashCardStorage: Storage<FlashCard>): ViewModel() {
    private val _flashCards = MutableStateFlow<List<FlashCard>>(emptyList())
    val flashCards: StateFlow<List<FlashCard>> get() = _flashCards

    private val _selectedFlashCard = MutableStateFlow<FlashCard?>(null)
    val selectedFlashCard: StateFlow<FlashCard?> = _selectedFlashCard

    fun getFlashCards() = viewModelScope.launch {
        flashCardStorage.getAll().catch { Log.e("FLASH_CARD_VIEW_MODEL", it.toString()) }.collect{_flashCards.emit(it)}
    }

    fun loadDefaultFlashCardsIfNoneExist() = viewModelScope.launch {
        val currentFlashCards = flashCardStorage.getAll().first()
        if(currentFlashCards.isEmpty()) {
            Log.d("FLASH_CARD_VIEW_MODEL", "Inserting default flash cards...")
            flashCardStorage.insertAll(FlashCard.getFlashCards()).catch { Log.w("FLASH_CARD_VIEW_MODEL", "Could not insert default flash cards")
                }.collect {
                Log.d("FLASH_CARD_VIEW_MODEL", "Default flash cards inserted successfully")
                _flashCards.emit(FlashCard.getFlashCards())
            }
        }
    }

    fun createFlashCard(question: String, answers: List<String>, correctAnswerIndex: Int) = viewModelScope.launch {
        val flashCard = FlashCard(id = Random.nextInt(0, Int.MAX_VALUE), question = question, answers = answers,
            correctAnswerIndex = correctAnswerIndex)
        flashCardStorage.insert(flashCard).catch { Log.e("FLASH_CARD_VIEW_MODEL", "Could not insert flash card") }.collect()
        flashCardStorage.getAll().catch { Log.e("FLASH_CARD_VIEW_MODEL", it.toString()) }.collect{_flashCards.emit(it)}
    }

    fun deleteFlashCard(flashCardId: Int) = viewModelScope.launch {
        flashCardStorage.delete(flashCardId).catch { Log.e("FLASH_CARD_VIEW_MODEL", "Could not delete flash card") }.collect()
        flashCardStorage.getAll().catch { Log.e("FLASH_CARD_VIEW_MODEL", it.toString()) }.collect{_flashCards.emit(it)}
    }
}