package ir.the_code.guardify.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val tokenKey = stringPreferencesKey("token")
        private val usernameKey = stringPreferencesKey("username")
    }

    val username: Flow<String?>
        get() = dataStore.data.map {
            it[usernameKey]
        }

    val token: Flow<String?>
        get() = dataStore.data.map {
            it[tokenKey]
        }

    suspend fun saveUserinfo(username: String, token: String) {
        dataStore.edit {
            it[usernameKey] = username
            it[tokenKey] = token
        }
    }

    suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }
}