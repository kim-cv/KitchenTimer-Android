package com.funkyqubits.kitchentimer.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.funkyqubits.kitchentimer.Controller.NotificationController;
import com.funkyqubits.kitchentimer.R;

/**
 * Triggers when alarm is complete
 */
public class TimerCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationController notificationController = new NotificationController(context);

        String paramIdKey = context.getString(R.string.notifications_parameter_id_key);
        String paramTitleKey = context.getString(R.string.notifications_parameter_title_key);
        String paramDescriptionKey = context.getString(R.string.notifications_parameter_description_key);

        int id = intent.getIntExtra(paramIdKey, 0);
        String title = intent.getStringExtra(paramTitleKey);
        String description = intent.getStringExtra(paramDescriptionKey);
        
        notificationController.ScheduleNotification(id, title, description);
    }
}
