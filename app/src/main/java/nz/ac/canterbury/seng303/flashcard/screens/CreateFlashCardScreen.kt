package nz.ac.canterbury.seng303.flashcard.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFlashCard(
    navController: NavController,
    question: String,
    onQuestionChange: (String) -> Unit,
    answers: List<String>,
    onAnswersChange: (List<String>) -> Unit,
    correctAnswerIndex: Int,
    onCorrectAnswerIndexChange: (Int) -> Unit,
    createFlashCardFn: (String, List<String>, Int) -> Unit
) {
    val context = LocalContext.current

    // Initialize screen with empty fields
    LaunchedEffect(Unit) {
        onQuestionChange("")
        onAnswersChange(List(3) { "" })
        onCorrectAnswerIndexChange(-1)
    }

    // Validate data
    val isSaveEnabled = question.isNotBlank() &&
            answers.any { it.isNotBlank() } &&
            correctAnswerIndex != -1 &&
            answers[correctAnswerIndex].isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = question,
            onValueChange = { onQuestionChange(it) },
            label = { Text("Question") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        answers.forEachIndexed { index, answer ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = answer,
                    onValueChange = { newAnswer ->
                        val updatedAnswers = answers.toMutableList().apply { this[index] = newAnswer }
                        onAnswersChange(updatedAnswers)
                    },
                    label = { Text("Answer ${index + 1}") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Checkbox(
                    checked = index == correctAnswerIndex,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            onCorrectAnswerIndexChange(index)
                        }
                    }
                )
                IconButton(
                    onClick = {
                        val updatedAnswers = answers.toMutableList().apply { removeAt(index) }
                        val newCorrectAnswerIndex = when {
                            index == correctAnswerIndex -> -1
                            index < correctAnswerIndex -> correctAnswerIndex - 1
                            else -> correctAnswerIndex
                        }
                        onAnswersChange(updatedAnswers)
                        onCorrectAnswerIndexChange(newCorrectAnswerIndex)
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
                val updatedAnswers = answers.toMutableList().apply { add("") }
                onAnswersChange(updatedAnswers)
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Add option")
        }

        Button(
            onClick = {
                if (isSaveEnabled) {
                    createFlashCardFn(question, answers, correctAnswerIndex)
                    Toast.makeText(
                        context,
                        "Flashcard created successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate("home")
                } else {
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
