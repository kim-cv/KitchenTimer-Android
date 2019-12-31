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

    companion object {
        fun startService(context: Context, timerTitle: String) {
            val startIntent = buildStartServiceIntent(context, timerTitle)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, AlarmAudioService::class.java)
            context.stopService(stopIntent)
        }

        private fun buildStartServiceIntent(context: Context, timerTitle: String): Intent {
            val paramTitleKey = context.getString(R.string.notifications_parameter_title_key)
            val startIntent = Intent(context, AlarmAudioService::class.java)
            startIntent.putExtra(paramTitleKey, timerTitle)
            return startIntent
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        InitMediaPlayer()
        // Only start media player if not playing
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }

        val paramTitleKey = getString(R.string.notifications_parameter_title_key)
        val timerTitle = intent?.getStringExtra(paramTitleKey) ?: ""

        // Oreo API 26+ requires a foreground notification for a service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = NotificationController(this).CreateForegroundNotification(timerTitle)
            startForeground(1, notification)
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
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }
}