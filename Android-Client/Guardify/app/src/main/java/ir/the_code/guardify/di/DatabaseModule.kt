package ir.the_code.guardify.di

import androidx.room.Room
import ir.the_code.guardify.data.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "db"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }
    single { get<AppDatabase>().blockedPhonesDao() }
}