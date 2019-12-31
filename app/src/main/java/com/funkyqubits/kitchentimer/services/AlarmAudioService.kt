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
    private var mediaPlayer: MediaPlayer? = null
    private val completedTimers = mutableListOf<String>()

    companion object {
        fun startService(context: Context, timerTitle: String) {
            val startIntent = Intent(context, AlarmAudioService::class.java)
            val paramTitleKey = context.getString(R.string.notifications_parameter_title_key)
            startIntent.putExtra("action", "start")
            startIntent.putExtra(paramTitleKey, timerTitle)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun observed(context: Context) {
            val intent = Intent(context, AlarmAudioService::class.java)
            intent.putExtra("action", "observed")
            ContextCompat.startForegroundService(context, intent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, AlarmAudioService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra("action")

        if (action == "start") {
            InitMediaPlayer()

            val paramTitleKey = getString(R.string.notifications_parameter_title_key)
            val timerTitle = intent.getStringExtra(paramTitleKey) ?: ""
            if (timerTitle.isNotEmpty()) {
                completedTimers.add(timerTitle)
            }

            StartSound()

            // Oreo API 26+ requires a foreground notification for a service
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val description = createNotificationDescription()
                val notification = NotificationController(this).CreateForegroundNotification(description)
                startForeground(1, notification)
            }
        } else if (action == "observed") {
            completedTimers.clear()
            StopSound()
        } else {
            //TODO
            throw Exception("Something went wrong")
        }

        return START_NOT_STICKY
    }

    private fun InitMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarmnext)
            mediaPlayer?.isLooping = true
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        completedTimers.clear()
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }

    private fun StartSound() {
        // Only start media player if not playing
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    private fun StopSound() {
        mediaPlayer?.stop()
    }

    private fun createNotificationDescription(): String {
        if (completedTimers.count() > 1) {
            return "Multiple timers are completed."
        } else if (completedTimers.count() == 1) {
            return "${completedTimers.first()} is completed."
        } else {
            return "A timer is completed."
        }
    }
}