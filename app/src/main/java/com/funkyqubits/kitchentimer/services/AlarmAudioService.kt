package com.funkyqubits.kitchentimer.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.funkyqubits.kitchentimer.Controller.NotificationController
import com.funkyqubits.kitchentimer.R

class AlarmAudioService : Service() {
    private var isServiceRunning: Boolean = false
    private var mediaPlayer: MediaPlayer? = null
    private val completedTimers = mutableListOf<String>()
    private var notificationController: NotificationController? = null

    companion object {
        fun startService(context: Context) {
            val startIntent = Intent(context, AlarmAudioService::class.java)
            startIntent.putExtra("action", "start")
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun timerComplete(context: Context, timerTitle: String) {
            val intent = Intent(context, AlarmAudioService::class.java)
            val paramTitleKey = context.getString(R.string.notifications_parameter_title_key)
            intent.putExtra("action", "timerComplete")
            intent.putExtra(paramTitleKey, timerTitle)
            ContextCompat.startForegroundService(context, intent)
        }

        fun timersInFocus(context: Context) {
            // This is called when we got the user attention, we can assume they know of completed timers and we can stop the alarm sound
            val intent = Intent(context, AlarmAudioService::class.java)
            intent.putExtra("action", "timersInFocus")
            ContextCompat.startForegroundService(context, intent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, AlarmAudioService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        InitNotificationController()
        InitMediaPlayer()
        val action = intent?.getStringExtra("action")

        when (action) {
            "start" -> {
                start()
            }
            "timerComplete" -> {
                timerComplete(intent)
            }
            "timersInFocus" -> {
                timersInFocus()
            }
            else -> {
                //TODO
                throw Exception("Something went wrong")
            }
        }

        return START_NOT_STICKY
    }

    //#region Service Actions
    private fun start() {
        if (isServiceRunning) {
            return
        }

        isServiceRunning = true
        updateNotificationDescription()
    }

    private fun timerComplete(intent: Intent) {
        val paramTitleKey = getString(R.string.notifications_parameter_title_key)
        val timerTitle = intent.getStringExtra(paramTitleKey) ?: ""
        if (timerTitle.isNotEmpty()) {
            completedTimers.add(timerTitle)
        }
        StartSound()
        updateNotificationDescription()
    }

    private fun timersInFocus() {
        StopSound()
        completedTimers.clear()
        updateNotificationDescription()
    }
    //#endregion


    private fun InitMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarmnext)
            mediaPlayer?.isLooping = true
        }
    }

    private fun InitNotificationController() {
        if (notificationController == null) {
            notificationController = NotificationController(this)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        completedTimers.clear()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        notificationController?.CancelNotification(1)
    }

    private fun StartSound() {
        // Only start media player if not playing
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    private fun StopSound() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
        }
    }

    private fun createNotificationDescription(): String {
        return when {
            completedTimers.count() > 1 -> {
                "Multiple timers are completed."
            }
            completedTimers.count() == 1 -> {
                "${completedTimers.first()} is completed."
            }
            else -> {
                "You will be notified when a timer is complete."
            }
        }
    }

    private fun updateNotificationDescription() {
        if (!isServiceRunning) {
            return
        }

        val description = createNotificationDescription()
        // Oreo API 26+ requires a foreground notification for a service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = notificationController?.CreateForegroundNotification(description)
            startForeground(1, notification)
        } else {
            val notification = notificationController?.CreateForegroundNotification(description)
            startForeground(1, notification)
        }
    }
}