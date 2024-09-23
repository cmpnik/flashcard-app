package nz.ac.canterbury.seng303.flashcard.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.flashcard.viewmodels.PlayFlashCardsViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close

@Composable
fun ResultsScreen(
    navController: NavController,
    playFlashCardsViewModel: PlayFlashCardsViewModel
) {
    val flashCards by playFlashCardsViewModel.flashCards.collectAsState()
    val selectedAnswers by playFlashCardsViewModel.selectedAnswers.collectAsState()
    val correctAnswersCount by playFlashCardsViewModel.correctAnswersCount.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Results",
            style = MaterialTheme.typography.headlineMedium
        )
        flashCards.forEachIndexed { index, flashCard ->
            val selectedAnswer = selectedAnswers[index]
            val isCorrect = flashCard.correctAnswerIndex == flashCard.answers.indexOf(selectedAnswer)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = flashCard.question)
                    Text(text = "Your answer: $selectedAnswer")
                }
                Icon(
                    imageVector = if (isCorrect) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = if (isCorrect) "Correct" else "Incorrect"
                )
            }
        }
        Text(
            text = "Total correct: $correctAnswersCount out of ${flashCards.size}",
            style = MaterialTheme.typography.bodyLarge
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { navController.navigate("Home") }
            ) {
                Text("Back")
            }
            Button(
                onClick = { navController.navigate("PlayFlashCards") },
            ) {
                Text("Play")
            }
        }
    }
}
