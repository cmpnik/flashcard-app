package nz.ac.canterbury.seng303.flashcard.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.flashcard.viewmodels.EditFlashCardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFlashCard(
    navController: NavController,
    viewModel: EditFlashCardViewModel,
    flashCardId: Int
) {
    val context = LocalContext.current
    val flashCard by viewModel.flashCard.collectAsState()

    var tempQuestion by rememberSaveable { mutableStateOf(flashCard?.question ?: "") }
    var tempAnswers by rememberSaveable { mutableStateOf(flashCard?.answers?.toMutableList() ?: mutableListOf()) }
    var tempCorrectAnswerIndex by rememberSaveable { mutableStateOf(flashCard?.correctAnswerIndex ?: -1) }

    // Load flashcard when flashCardId changes
    LaunchedEffect(flashCardId) {
        viewModel.loadFlashCard(flashCardId)
    }

    // Update local state when flashCard state changes
    LaunchedEffect(flashCard) {
        flashCard?.let {
            tempQuestion = it.question
            tempAnswers = it.answers.toMutableList()
            tempCorrectAnswerIndex = it.correctAnswerIndex
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = tempQuestion,
            onValueChange = { newQuestion ->
                tempQuestion = newQuestion
            },
            label = { Text("Question") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        tempAnswers.forEachIndexed { index, answer ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = answer,
                    onValueChange = { newAnswer ->
                        val updatedAnswers = tempAnswers.toMutableList().apply { this[index] = newAnswer }
                        tempAnswers = updatedAnswers
                    },
                    label = { Text("Answer ${index + 1}") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Checkbox(
                    checked = index == tempCorrectAnswerIndex,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            tempCorrectAnswerIndex = index
                        }
                    }
                )
                IconButton(
                    onClick = {
                        val updatedAnswers = tempAnswers.toMutableList().apply { removeAt(index) }
                        val newCorrectAnswerIndex = when {
                            index == tempCorrectAnswerIndex -> -1
                            index < tempCorrectAnswerIndex -> tempCorrectAnswerIndex - 1
                            else -> tempCorrectAnswerIndex
                        }
                        tempAnswers = updatedAnswers
                        tempCorrectAnswerIndex = newCorrectAnswerIndex
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove answer",
                        tint = Color.Red
                    )
                }
            }
        }

        OutlinedButton(
            onClick = {
                val updatedAnswers = tempAnswers.toMutableList().apply { add("") }
                tempAnswers = updatedAnswers
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Add option")
        }

        Button(
            onClick = {
                // Validate data
                val isSaveEnabled = tempQuestion.isNotBlank() &&
                        tempAnswers.any { it.isNotBlank() } &&
                        tempCorrectAnswerIndex != -1 &&
                        tempAnswers.getOrNull(tempCorrectAnswerIndex)?.isNotBlank() == true

                if (isSaveEnabled) {
                    // Update flashcard if valid
                    viewModel.updateFlashCard(
                        question = tempQuestion,
                        answers = tempAnswers,
                        correctAnswerIndex = tempCorrectAnswerIndex
                    )
                    Toast.makeText(
                        context,
                        "Flash card successfully updated!",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate("FlashCardList")
                } else {
                    // Display a toast message if not valid
                    Toast.makeText(
                        context,
                        "Please make sure all fields are valid.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        ) {
            Text(text = "Save")
        }
    }
}
