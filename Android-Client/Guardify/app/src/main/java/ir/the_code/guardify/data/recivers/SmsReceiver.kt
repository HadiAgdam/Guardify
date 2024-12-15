package ir.the_code.guardify.data.recivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ir.the_code.guardify.data.workers.InternetDependentWorker

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        messages?.forEach { sms ->
            Log.d(
                "dsfdssfs",
                "onReceive: ${sms.displayOriginatingAddress} ${sms.displayMessageBody}"
            )
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val data = Data.Builder()
                .putString("text", sms.displayMessageBody)
                .putString("from", sms.displayOriginatingAddress)
                .putString("type", "MESSAGE")
                .build()

            val workRequest = OneTimeWorkRequestBuilder<InternetDependentWorker>()
                .setConstraints(constraints)
                .setInputData(data)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
//
//                apiService.checkMessageHasDangerous(
//                    MessageBody(
//                        from = sms.displayOriginatingAddress ?: "",
//                        text = sms.displayMessageBody ?: ""
//                    )
//                ).onSuccess {
//
//                }
        }
    }
}