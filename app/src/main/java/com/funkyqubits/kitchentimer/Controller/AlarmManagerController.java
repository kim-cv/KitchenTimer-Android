package com.funkyqubits.kitchentimer.Controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.funkyqubits.kitchentimer.BroadcastReceivers.TimerCompleteReceiver;
import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.models.AlarmTimer;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;


public class AlarmManagerController {
    private Context Context;
    private AlarmManager AlarmManager;

    public AlarmManagerController(Context _context) {
        Context = _context;
        AlarmManager = (AlarmManager) Context.getSystemService(ALARM_SERVICE);
    }

    public void ScheduleAlarm(AlarmTimer alarmTimer, int secondsFromNow) {
        Intent intent = new Intent(Context, TimerCompleteReceiver.class);

        String paramIdKey = Context.getString(R.string.notifications_parameter_id_key);
        String paramTitleKey = Context.getString(R.string.notifications_parameter_title_key);

        intent.putExtra(paramIdKey, alarmTimer.ID);
        intent.putExtra(paramTitleKey, alarmTimer.Title);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(Context, alarmTimer.ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, secondsFromNow);

        AlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void CancelAlarm(int uniqueId) {
        /*
        With FLAG_NO_CREATE it will return null if the PendingIntent doesn't already exist. If it already exists it returns
        reference to the existing PendingIntent
        */
        Intent intent = new Intent(Context, TimerCompleteReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Context, uniqueId, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null) {
            AlarmManager.cancel(pendingIntent);
        }
    }
}
