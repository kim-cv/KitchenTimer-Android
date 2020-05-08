package com.funkyqubits.kitchentimer.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.services.AlarmAudioService;

/**
 * Triggers when alarm is complete
 */
public class TimerCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve intent data
        String paramIdKey = context.getString(R.string.notifications_parameter_id_key);

        int id = intent.getIntExtra(paramIdKey, 0);

        AlarmAudioService.Companion.timerComplete(context, id);
    }
}
