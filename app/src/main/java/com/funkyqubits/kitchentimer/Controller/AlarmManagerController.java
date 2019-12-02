package com.funkyqubits.kitchentimer.Controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.funkyqubits.kitchentimer.BroadcastReceivers.TimerCompleteReceiver;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;


public class AlarmManagerController {
    private Context Context;
    private AlarmManager AlarmManager;

    public AlarmManagerController(Context _context) {
        Context = _context;
        AlarmManager = (AlarmManager) Context.getSystemService(ALARM_SERVICE);
    }

    public void ScheduleAlarm(int uniqueId, int secondsFromNow) {
        Intent intent = new Intent(Context, TimerCompleteReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(Context, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, secondsFromNow);

        AlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
