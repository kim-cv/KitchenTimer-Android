package com.funkyqubits.kitchentimer.ui.timers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerClickObserver;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerClickedSubject;
import com.funkyqubits.kitchentimer.Models.AlarmTimer;
import com.funkyqubits.kitchentimer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class AlarmTimersAdapter extends RecyclerView.Adapter<AlarmTimersAdapter.ViewHolder> implements IAlarmTimerClickedSubject {
    private final List<IAlarmTimerClickObserver> ObservableItemClickedList = new ArrayList<>();
    private ArrayList<AlarmTimer> Dataset_alarmTimers;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout LinearLayout;
        public UUID AlarmTimerID;

        public ViewHolder(LinearLayout v) {
            super(v);
            LinearLayout = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AlarmTimersAdapter() {
    }

    public void SetData(ArrayList<AlarmTimer> alarmTimers) {
        Dataset_alarmTimers = alarmTimers;
        InitTimer();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final AlarmTimer alarmTimer = Dataset_alarmTimers.get(position);

        // Set AlarmTimerID onto the view
        holder.AlarmTimerID = alarmTimer.ID;

        // Find views
        TextView txtView_title = holder.LinearLayout.findViewById(R.id.timer_title);
        TextView txtView_progress = holder.LinearLayout.findViewById(R.id.timer_progress);

        // Set text values
        txtView_title.setText(alarmTimer.Title);
        txtView_progress.setText(alarmTimer.ReadableTimer);

        // Click listener
        holder.LinearLayout.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifyAlarmTimerStart(alarmTimer.ID);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Dataset_alarmTimers.size();
    }


    private void InitTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                for (int i = 0; i < Dataset_alarmTimers.size(); i++) {
                    AlarmTimer alarmTimer = Dataset_alarmTimers.get(i);
                    if (alarmTimer.AlarmTimerState == AlarmTimer.ALARMTIMER_STATE.RUNNING) {
                        alarmTimer.Tick();
                        notifyItemChanged(i);
                    }
                }
            }
        }, 0, 1000);//Update every second
    }


    //#region Subject/Observer
    @Override
    public void RegisterObserver(IAlarmTimerClickObserver observer) {
        if (!ObservableItemClickedList.contains(observer)) {
            ObservableItemClickedList.add(observer);
        }
    }

    @Override
    public void RemoveObserver(IAlarmTimerClickObserver observer) {
        if (ObservableItemClickedList.contains(observer)) {
            ObservableItemClickedList.remove(observer);
        }
    }

    @Override
    public void NotifyAlarmTimerStart(UUID alarmTimerID) {
        for (IAlarmTimerClickObserver observer : ObservableItemClickedList) {
            observer.OnStart(alarmTimerID);
        }
    }
    //#endregion
}
