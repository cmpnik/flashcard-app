import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.FlowPreview
import nz.ac.canterbury.seng303.flashcard.datastore.PersistentStorage
import nz.ac.canterbury.seng303.flashcard.datastore.Storage
import nz.ac.canterbury.seng303.flashcard.models.FlashCard
import nz.ac.canterbury.seng303.flashcard.viewmodels.FlashCardViewModel
import nz.ac.canterbury.seng303.flashcard.viewmodels.PlayFlashCardsViewModel
import nz.ac.canterbury.seng303.flashcard.viewmodels.EditFlashCardViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "flashCards_data")

@FlowPreview
val dataAccessModule = module {
    single<Storage<FlashCard>> {
        PersistentStorage(
            gson = get(),
            type = object : TypeToken<List<FlashCard>>() {}.type,
            preferenceKey = stringPreferencesKey("flashCards"),
            dataStore = androidContext().dataStore
        )
    }

    single { Gson() }

    viewModel {
        FlashCardViewModel(
            flashCardStorage = get()
        )
    }

    viewModel {
        PlayFlashCardsViewModel(
            flashCardStorage = get()
        )
    }

    viewModel { (flashCardId: Int) ->
        EditFlashCardViewModel(
            flashCardStorage = get(),
            flashCardId = flashCardId
        )
    }
}
