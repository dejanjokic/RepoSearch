package net.dejanjokic.reposearch

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import net.dejanjokic.reposearch.util.log.HyperlinkTimberTree
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(HyperlinkTimberTree())
        AndroidThreeTen.init(this)
    }
}