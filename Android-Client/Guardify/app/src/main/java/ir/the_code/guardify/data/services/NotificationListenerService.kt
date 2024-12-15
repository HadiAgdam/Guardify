package ir.the_code.guardify.data.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ir.the_code.guardify.data.workers.InternetDependentWorker

//
//val targetPackages: List<String>
//    get() =  listOf(
//        "com.telegram",
//        "com.instagram",
//        "com.whatsapp",
//        "com.messenger",
//        "com.snapchat",
//        "com.spotify",
//        "com.tiktok",
//        "com.zoom",
//        "com.pinterest",
//        "com.discord"
//    )

class MyNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let {
            val title = it.notification.extras.getString("android.title")
            val text = it.notification.extras.getString("android.text")
            Log.d("dsfsdfdf", "onNotificationPosted: $text")
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val data = Data.Builder()
                .putString("text", title ?: "")
                .putString("from", text)
                .putString("type", "NOTIFICATION")
                .build()

            val workRequest = OneTimeWorkRequestBuilder<InternetDependentWorker>()
                .setConstraints(constraints)
                .setInputData(data)
                .build()
            WorkManager.getInstance(this).enqueue(workRequest)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}