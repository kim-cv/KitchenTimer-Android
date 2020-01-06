package com.funkyqubits.kitchentimer.Controller

import android.app.Notification
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
    private val channelForegroundId: String

    enum class NOTIFICATION_TYPE {
        NORMAL,
        FOREGROUND
    }

    init {
        channelId = context.getString(R.string.notifications_channel_id)
        channelForegroundId = context.getString(R.string.notifications_channel_foreground_id)
        channelName = context.getString(R.string.notifications_channel_name)
        channelDescription = context.getString(R.string.notifications_channel_description)
    }

    public fun CreateNotificationChannels() {
        CreateNotificationChannel(NOTIFICATION_TYPE.FOREGROUND)
        CreateNotificationChannel(NOTIFICATION_TYPE.NORMAL)
    }

    private fun CreateNotificationChannel(channelType: NOTIFICATION_TYPE) {
        /*
            Create the NotificationChannel, but only on API 26+ because
            the NotificationChannel class is new and not in the support library
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = when (channelType) {
                NOTIFICATION_TYPE.FOREGROUND -> NotificationChannel(channelForegroundId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = channelDescription
                    setSound(null, null)
                }
                else -> NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = channelDescription
                    setSound(null, null)
                }
            }

            // Register the channel with the system
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    public fun CreateForegroundNotification(description: String): Notification? {
        val title = "Timers Status."
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
                context,
                0, notificationIntent, 0
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Notification.Builder(context, channelForegroundId)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSmallIcon(R.drawable.ic_timelapse_white_24dp)
                    .setContentIntent(pendingIntent)
                    .build()
        } else {
            return NotificationCompat.Builder(context, channelForegroundId)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSmallIcon(R.drawable.ic_timelapse_white_24dp)
                    .setContentIntent(pendingIntent)
                    .build()
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
                .setSmallIcon(R.drawable.ic_timelapse_white_24dp)
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

    public fun CancelNotification(_id: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(_id)
        }
    }
}