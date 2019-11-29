package com.funkyqubits.kitchentimer.ui.timers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.funkyqubits.kitchentimer.Models.AlarmTimer;
import com.funkyqubits.kitchentimer.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmTimersAdapter extends RecyclerView.Adapter<AlarmTimersAdapter.ViewHolder> {
    private ArrayList<AlarmTimer> Dataset_alarmTimers;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout LinearLayout;

        public ViewHolder(LinearLayout v) {
            super(v);
            LinearLayout = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AlarmTimersAdapter(ArrayList<AlarmTimer> alarmTimers) {
        Dataset_alarmTimers = alarmTimers;
        InitTimer();
        Dataset_alarmTimers.get(0).Start();
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

        TextView txtView_title = holder.LinearLayout.findViewById(R.id.timer_title);
        TextView txtView_progress = holder.LinearLayout.findViewById(R.id.timer_progress);

        txtView_title.setText(Dataset_alarmTimers.get(position).Title);
        txtView_progress.setText(Dataset_alarmTimers.get(position).ReadableTimer);

        holder.LinearLayout.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}
