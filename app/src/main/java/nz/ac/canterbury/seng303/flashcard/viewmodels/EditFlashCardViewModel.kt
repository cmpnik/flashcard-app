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

class EditFlashCardViewModel(
    private val flashCardStorage: Storage<FlashCard>,
    private val flashCardId: Int
) : ViewModel() {

    private val _flashCard = MutableStateFlow<FlashCard?>(null)
    val flashCard: StateFlow<FlashCard?> get() = _flashCard

    fun loadFlashCard(flashCardId: Int) = viewModelScope.launch {
        flashCardStorage.get { it.getIdentifier() == flashCardId }
            .catch { e -> Log.e("EditFlashCardViewModel", "Error loading flash card", e) }
            .collect { _flashCard.value = it }
    }

    fun updateFlashCard(question: String, answers: List<String>, correctAnswerIndex: Int) = viewModelScope.launch {
        _flashCard.value?.let { flashCard ->
            val updatedFlashCard = flashCard.copy(
                question = question,
                answers = answers,
                correctAnswerIndex = correctAnswerIndex
            )
            flashCardStorage.edit(flashCard.getIdentifier(), updatedFlashCard)
                .catch { e -> Log.e("EditFlashCardViewModel", "Error updating flash card", e) }
                .collect { _flashCard.value = updatedFlashCard }
        }
    }
}

