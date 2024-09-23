package nz.ac.canterbury.seng303.flashcard.screens

import android.content.Intent
import android.app.AlertDialog
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.flashcard.models.FlashCard
import nz.ac.canterbury.seng303.flashcard.viewmodels.FlashCardViewModel

@Composable
fun FlashCardList(navController: NavController, flashCardViewModel: FlashCardViewModel) {
    flashCardViewModel.getFlashCards()
    val flashCards: List<FlashCard> by flashCardViewModel.flashCards.collectAsState(emptyList())
    LazyColumn {
        items(flashCards) { flashcard ->
            FlashCardItem(navController = navController, flashCard = flashcard, deleteFlashCardFn = {id: Int -> flashCardViewModel.deleteFlashCard(id)})
            Divider() // Add a divider between items
        }
    }
}

@Composable
fun FlashCardItem(navController: NavController, flashCard: FlashCard, deleteFlashCardFn: (id: Int) -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Display question
        Column(
            modifier = Modifier
                .weight(3f)
                .fillMaxWidth()
        ) {
            Text(
                text = flashCard.question,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Display edit and delete buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .wrapContentSize()
        ) {
            IconButton(onClick = {
                val searchQuery = Uri.encode(flashCard.question)
                val searchIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$searchQuery"))
                context.startActivity(searchIntent)
            }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = Color.Blue
                )
            }
            IconButton(onClick = {
                navController.navigate("editFlashCard/${flashCard.id}")
            }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    tint = Color.Blue
                )
            }
            IconButton(onClick = {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Delete flash card?")
                    .setCancelable(false)
                    .setPositiveButton("Delete") { dialog, id ->
                        deleteFlashCardFn(flashCard.id)
                        Toast.makeText(context, "Flash card successfully deleted", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }
    }
}
