package com.funkyqubits.kitchentimer.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Triggers when alarm is complete
 */
public class TimerCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TimerCompleteReceiver", "Alarm Triggered");
        Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_SHORT).show();
    }
}
