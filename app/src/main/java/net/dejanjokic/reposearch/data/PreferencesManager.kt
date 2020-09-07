package net.dejanjokic.reposearch.data

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.dejanjokic.reposearch.core.Constants.APP.PREFS_SORT_INDEX_KEY
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ExperimentalCoroutinesApi
class PreferencesManager @Inject constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val SORT_INDEX = preferencesKey<Int>(PREFS_SORT_INDEX_KEY)
    }

    fun saveSortIndex(index: Int) {
        GlobalScope.launch {
            dataStore.edit { preferences ->
                preferences[SORT_INDEX] = index
            }
        }
    }

    fun sortIndexFlow(): Flow<Int> = dataStore.data.map { preferences ->
        preferences[SORT_INDEX] ?: 0
    }
}