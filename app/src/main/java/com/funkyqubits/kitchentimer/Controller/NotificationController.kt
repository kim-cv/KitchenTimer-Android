package com.funkyqubits.kitchentimer.Controller

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.funkyqubits.kitchentimer.MainActivity
import com.funkyqubits.kitchentimer.R

class NotificationController constructor(_context: Context) {
    private val context: Context = _context
    private val channelId: String
    private val channelName: String
    private val channelDescription: String

    init {
        channelId = context.getString(R.string.notifications_channel_id)
        channelName = context.getString(R.string.notifications_channel_name)
        channelDescription = context.getString(R.string.notifications_channel_description)
    }

    public fun CreateNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    public fun ScheduleNotification(_id: Int, _title: String, _message: String) {
        // Build intent to open app with activity when tapping notification
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        // Build notification
        val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_settings_white_24dp)
                .setContentTitle(_title)
                .setContentText(_message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)

        // Show notification
        with(NotificationManagerCompat.from(context)) {
            notify(_id, builder.build())
        }
    }
}