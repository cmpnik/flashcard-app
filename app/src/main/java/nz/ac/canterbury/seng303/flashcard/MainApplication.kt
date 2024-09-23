package nz.ac.canterbury.seng303.flashcard

import android.app.Application
import dataAccessModule
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication: Application() {
    @OptIn(FlowPreview::class)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(dataAccessModule)
        }
    }
}