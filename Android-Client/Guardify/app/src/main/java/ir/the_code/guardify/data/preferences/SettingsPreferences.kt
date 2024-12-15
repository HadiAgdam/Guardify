package ir.the_code.guardify.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsPreferences(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val darkTheme = booleanPreferencesKey("dark_theme")
    }


    val isDarkTheme: Flow<Boolean?>
        get() = dataStore.data.map {
            it[darkTheme]
        }

    suspend fun changeTheme(isDark: Boolean) {
        dataStore.edit {
            it[darkTheme] = isDark
        }
    }
}