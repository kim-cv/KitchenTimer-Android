package com.funkyqubits.kitchentimer.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.funkyqubits.kitchentimer.Controller.NotificationController
import com.funkyqubits.kitchentimer.R

class AlarmAudioService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val completedTimers = mutableListOf<String>()
    private val runningTimers = mutableListOf<String>()
    private var notificationController: NotificationController? = null

    enum class SERVICE_ACTION {
        START,
        ADD_RUNNING_TIMER,
        REMOVE_RUNNING_TIMER,
        TIMER_COMPLETE,
        TIMERS_IN_FOCUS,
        STOP
    }

    companion object {
        private var isServiceRunning: Boolean = false

        fun startService(context: Context) {
            if (isServiceRunning) return

            val intent = createAlarmAudioServiceIntent(context, SERVICE_ACTION.START)
            ContextCompat.startForegroundService(context, intent)
        }

        fun addRunningTimer(context: Context, timerTitle: String) {
            if (!isServiceRunning) return

            val intent = createAlarmAudioServiceIntent(context, SERVICE_ACTION.ADD_RUNNING_TIMER)
            val paramTitleKey = context.getString(R.string.notifications_parameter_title_key)
            intent.putExtra(paramTitleKey, timerTitle)
            ContextCompat.startForegroundService(context, intent)
        }

        fun removeRunningTimer(context: Context, timerTitle: String) {
            if (!isServiceRunning) return

            val intent = createAlarmAudioServiceIntent(context, SERVICE_ACTION.REMOVE_RUNNING_TIMER)
            val paramTitleKey = context.getString(R.string.notifications_parameter_title_key)
            intent.putExtra(paramTitleKey, timerTitle)
            ContextCompat.startForegroundService(context, intent)
        }

        fun timerComplete(context: Context, timerTitle: String) {
            if (!isServiceRunning) return

            val intent = createAlarmAudioServiceIntent(context, SERVICE_ACTION.TIMER_COMPLETE)
            val paramTitleKey = context.getString(R.string.notifications_parameter_title_key)
            intent.putExtra(paramTitleKey, timerTitle)
            ContextCompat.startForegroundService(context, intent)
        }

        fun timersInFocus(context: Context) {
            if (!isServiceRunning) return

            // This is called when we got the user attention, we can assume they know of completed timers and we can stop the alarm sound
            val intent = createAlarmAudioServiceIntent(context, SERVICE_ACTION.TIMERS_IN_FOCUS)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stopService(context: Context) {
            if (!isServiceRunning) return

            val intent = createAlarmAudioServiceIntent(context, SERVICE_ACTION.STOP)
            ContextCompat.startForegroundService(context, intent)
        }

        private fun createAlarmAudioServiceIntent(context: Context, action: SERVICE_ACTION): Intent {
            val intent = Intent(context, AlarmAudioService::class.java)
            intent.putExtra("action", action)
            return intent
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getSerializableExtra("action") as SERVICE_ACTION

        when (action) {
            SERVICE_ACTION.START -> start()
            SERVICE_ACTION.ADD_RUNNING_TIMER -> addRunningTimer(intent)
            SERVICE_ACTION.REMOVE_RUNNING_TIMER -> removeRunningTimer(intent)
            SERVICE_ACTION.TIMER_COMPLETE -> timerComplete(intent)
            SERVICE_ACTION.TIMERS_IN_FOCUS -> timersInFocus()
            SERVICE_ACTION.STOP -> stop()
        }

        return START_STICKY
    }

    //#region Service Actions
    private fun start() {
        if (isServiceRunning) {
            return
        }

        isServiceRunning = true
        InitNotificationController()
        updateNotificationDescription()
        InitMediaPlayer()
    }

    private fun addRunningTimer(intent: Intent) {
        val paramTitleKey = getString(R.string.notifications_parameter_title_key)
        val timerTitle = intent.getStringExtra(paramTitleKey) ?: ""
        if (timerTitle.isNotEmpty()) {
            runningTimers.add(timerTitle)
        }
    }

    private fun removeRunningTimer(intent: Intent) {
        val paramTitleKey = getString(R.string.notifications_parameter_title_key)
        val timerTitle = intent.getStringExtra(paramTitleKey) ?: ""
        if (timerTitle.isNotEmpty()) {
            runningTimers.remove(timerTitle)
        }
    }

    private fun timerComplete(intent: Intent) {
        val paramTitleKey = getString(R.string.notifications_parameter_title_key)
        val timerTitle = intent.getStringExtra(paramTitleKey) ?: ""
        if (timerTitle.isNotEmpty()) {
            completedTimers.add(timerTitle)
            runningTimers.removeAll{ it == timerTitle }
        }
        StartSound()
        updateNotificationDescription()
    }

    private fun timersInFocus() {
        StopSound()
        completedTimers.clear()
        updateNotificationDescription()
    }

    private fun stop() {
        if (runningTimers.count() > 0) {
            return
        }

        isServiceRunning = false
        runningTimers.clear()
        completedTimers.clear()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        notificationController?.CancelNotification(1)
        notificationController = null
        stopSelf()
    }
    //#endregion


    private fun InitMediaPlayer() {
        if (mediaPlayer == null) {
            val attr = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .build()
            mediaPlayer = MediaPlayer.create(this, R.raw.alarmnext, attr, 0)
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

    private fun StartSound() {
        // Only start media player if not playing
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    private fun StopSound() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer?.prepare()
            mediaPlayer?.pause()
            //mediaPlayer?.stop()
            //mediaPlayer?.prepare()
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