package ir.the_code.guardify.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import ir.the_code.guardify.data.preferences.UserPreferences
import ir.the_code.guardify.data.preferences.SettingsPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val Context.preferences: DataStore<Preferences> by preferencesDataStore("preferences")

val preferencesModule = module {
    single<DataStore<Preferences>> {
        androidContext().preferences
    }
    singleOf(::UserPreferences)
    singleOf(::SettingsPreferences)
}