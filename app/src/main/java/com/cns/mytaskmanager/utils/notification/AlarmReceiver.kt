package com.cns.mytaskmanager.utils.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.cns.mytaskmanager.MainActivity
import com.cns.mytaskmanager.R
class AlarmReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context?, intent: Intent?) {
        println("Alarm triggered")
        val i = Intent(context, MainActivity::class.java)
        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context!!, "task-manager-notification")
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle("Hello captain")
            .setContentText("Come and complete your task")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(123, builder.build())
            } else {
//                Toast.makeText(
//                    context,
//                    "POST_NOTIFICATION permission not granted",
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        } else {
            notificationManager.notify(123, builder.build())
        }
    }
}