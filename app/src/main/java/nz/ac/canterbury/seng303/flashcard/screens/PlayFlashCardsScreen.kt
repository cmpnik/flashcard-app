package nz.ac.canterbury.seng303.flashcard.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.flashcard.viewmodels.PlayFlashCardsViewModel

@Composable
fun PlayFlashCards(navController: NavController, playFlashCardsViewModel: PlayFlashCardsViewModel) {
    val flashCards by playFlashCardsViewModel.flashCards.collectAsState()
    val currentIndex by playFlashCardsViewModel.currentIndex.collectAsState()
    val selectedAnswers by playFlashCardsViewModel.selectedAnswers.collectAsState()

    val context = LocalContext.current

    // Load flashcards
    LaunchedEffect(Unit) {
        playFlashCardsViewModel.loadFlashCards()
    }

    val flashCard = flashCards.getOrNull(currentIndex)
    val selectedAnswer = selectedAnswers[currentIndex]

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        if (flashCard != null) {
            Text(
                text = flashCard.question,
                style = MaterialTheme.typography.headlineMedium
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
            flashCard.answers.forEach { answer ->
                if (answer == selectedAnswer) {
                    Button(
                        onClick = {
                            playFlashCardsViewModel.selectAnswer(answer)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(answer)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            playFlashCardsViewModel.selectAnswer(answer)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(answer)
                    }
                }
            }
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        // Check if the selected answer is correct
                        val isCorrect = flashCard.correctAnswerIndex == flashCard.answers.indexOf(selectedAnswer)
                        val toastMessage = if (isCorrect) {
                            "Correct answer!"
                        } else {
                            "Incorrect answer."
                        }
                        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()

                        // Load the next flash card
                        if (currentIndex < flashCards.size - 1) {
                            playFlashCardsViewModel.nextFlashCard()
                        } else {
                            playFlashCardsViewModel.evaluateResults()
                            navController.navigate("ResultsScreen")
                        }
                    },
                    enabled = selectedAnswer != null
                ) {
                    Text(if (currentIndex < flashCards.size - 1) "Next" else "Finish")
                }
            }
        } else {
            Text(
                text = "No flashcards available.",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
