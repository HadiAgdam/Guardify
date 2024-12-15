package ir.the_code.guardify.data.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.provider.Telephony
import android.util.Log
import ir.the_code.guardify.R
import ir.the_code.guardify.data.recivers.SmsReceiver

class MySmsListenerService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("dsfdssfs", "onStartCommand: start")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(
                1,
                Notification.Builder(this, "app")
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.protecting))
                    .setSmallIcon(R.drawable.logo)
                    .build()
            )
        } else {
            startForeground(
                1,
                Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.protecting))
                    .setSmallIcon(R.drawable.logo)
                    .build()
            )
        }
        registerReceiver(
            SmsReceiver(),
            IntentFilter(
                Telephony.Sms.Intents.SMS_RECEIVED_ACTION
            )
        )
        return START_STICKY
    }

    override fun onDestroy() {
        stopSelf()
        super.onDestroy()
    }
}