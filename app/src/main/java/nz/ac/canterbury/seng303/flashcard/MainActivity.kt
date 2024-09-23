package nz.ac.canterbury.seng303.flashcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import nz.ac.canterbury.seng303.flashcard.screens.CreateFlashCard
import nz.ac.canterbury.seng303.flashcard.screens.EditFlashCard
import nz.ac.canterbury.seng303.flashcard.screens.FlashCardList
import nz.ac.canterbury.seng303.flashcard.screens.PlayFlashCards
import nz.ac.canterbury.seng303.flashcard.screens.ResultsScreen
import nz.ac.canterbury.seng303.flashcard.ui.theme.Lab2Theme
import nz.ac.canterbury.seng303.flashcard.viewmodels.CreateFlashCardViewModel
import nz.ac.canterbury.seng303.flashcard.viewmodels.EditFlashCardViewModel
import nz.ac.canterbury.seng303.flashcard.viewmodels.FlashCardViewModel
import nz.ac.canterbury.seng303.flashcard.viewmodels.PlayFlashCardsViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import org.koin.androidx.viewmodel.ext.android.viewModel as koinViewModel

class MainActivity : ComponentActivity() {
    private val flashCardViewModel: FlashCardViewModel by koinViewModel()
    private val playFlashCardsViewModel: PlayFlashCardsViewModel by koinViewModel()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        flashCardViewModel.loadDefaultFlashCardsIfNoneExist()
        setContent {
            Lab2Theme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Flash Card App") },
                            navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )
                    }
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        val createFlashCardViewModel: CreateFlashCardViewModel = viewModel()
                        NavHost(navController = navController, startDestination = "Home") {
                            composable("Home") {
                                Home(navController = navController)
                            }
                            composable(
                                "editFlashCard/{flashCardId}",
                                arguments = listOf(navArgument("flashCardId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val flashCardId = backStackEntry.arguments?.getInt("flashCardId") ?: return@composable
                                val editFlashCardViewModel: EditFlashCardViewModel = getViewModel(parameters = { parametersOf(flashCardId) })
                                EditFlashCard(
                                    navController = navController,
                                    viewModel = editFlashCardViewModel,
                                    flashCardId = flashCardId
                                )
                            }
                            composable("PlayFlashCards") {
                                PlayFlashCards(navController, playFlashCardsViewModel)
                            }
                            composable("FlashCardList") {
                                FlashCardList(navController, flashCardViewModel)
                            }
                            composable("CreateFlashCard") {
                                CreateFlashCard(
                                    navController = navController,
                                    question = createFlashCardViewModel.question,
                                    onQuestionChange = { newQuestion -> createFlashCardViewModel.updateQuestion(newQuestion) },
                                    answers = createFlashCardViewModel.answers,
                                    onAnswersChange = { newAnswers -> createFlashCardViewModel.updateAnswers(newAnswers) },
                                    correctAnswerIndex = createFlashCardViewModel.correctAnswerIndex,
                                    onCorrectAnswerIndexChange = { newIndex -> createFlashCardViewModel.updateCorrectAnswerIndex(newIndex) },
                                    createFlashCardFn = { question, answers, correctAnswerIndex -> flashCardViewModel.createFlashCard(question, answers, correctAnswerIndex) }
                                )
                            }
                            composable("ResultsScreen") {
                                ResultsScreen(navController = navController, playFlashCardsViewModel = playFlashCardsViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Home(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Flash Cards App")
        Button(onClick = { navController.navigate("CreateFlashCard") }) {
            Text("Create Flash Card")
        }
        Button(onClick = { navController.navigate("PlayFlashCards") }) {
            Text("Play Flash Cards")
        }
        Button(onClick = { navController.navigate("FlashCardList") }) {
            Text("Flash Card List")
        }
    }
}
