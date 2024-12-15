package ir.the_code.guardify.viewmodels.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.the_code.guardify.data.preferences.SettingsPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {
    val isDarkTheme = settingsPreferences.isDarkTheme

    fun changeTheme(isDark: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        settingsPreferences.changeTheme(isDark)
    }
}