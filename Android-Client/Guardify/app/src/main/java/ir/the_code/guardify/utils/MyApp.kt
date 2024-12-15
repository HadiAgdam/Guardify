package ir.the_code.guardify.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import ir.the_code.guardify.di.databaseModule
import ir.the_code.guardify.di.networkModules
import ir.the_code.guardify.di.otherModules
import ir.the_code.guardify.di.preferencesModule
import ir.the_code.guardify.di.repositoryModule
import ir.the_code.guardify.di.viewModelModules
import ir.the_code.guardify.di.workerModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        startKoin {
            androidContext(this@MyApp)
            workManagerFactory()
            modules(
                viewModelModules,
                otherModules,
                repositoryModule,
                networkModules,
                preferencesModule,
                databaseModule,
                workerModule
            )
        }
        NotificationManagerCompat.from(this).also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.createNotificationChannel(
                    NotificationChannel(
                        "app",
                        "test",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                )
            }
        }
        super.onCreate()
    }
}