package com.funkyqubits.kitchentimer.ui.timers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.funkyqubits.kitchentimer.BR;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerClickObserver;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerClickedSubject;
import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.funkyqubits.kitchentimer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AlarmTimersAdapter extends RecyclerView.Adapter<AlarmTimersAdapter.ViewHolder> implements IAlarmTimerClickedSubject {
    private final List<IAlarmTimerClickObserver> ObservableItemClickedList = new ArrayList<>();
    private ArrayList<AlarmTimer> Dataset_alarmTimers;
    private Fragment ContainingFragment;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View View;
        public int AlarmTimerID;
        public ViewDataBinding binding;

        public ViewHolder(View view, Fragment fragment) {
            super(view);
            View = view;
            binding = DataBindingUtil.bind(view);
            binding.setLifecycleOwner(fragment); // Necessary for LiveData and MutableLiveData to work
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AlarmTimersAdapter(Fragment _fragment) {
        ContainingFragment = _fragment;
    }

    public void SetData(ArrayList<AlarmTimer> alarmTimers) {
        Dataset_alarmTimers = alarmTimers;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.timer_list_item, parent, false);
        return new ViewHolder(view, ContainingFragment);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final AlarmTimer alarmTimer = Dataset_alarmTimers.get(position);

        // Bind model to view
        holder.binding.setVariable(BR.alarmTimer, alarmTimer);
        holder.binding.executePendingBindings();

        // Set AlarmTimerID onto the view
        holder.AlarmTimerID = alarmTimer.ID;

        // Find views
        TextView txtView_title = holder.View.findViewById(R.id.timer_title);
        Button btn_timer_start = holder.View.findViewById(R.id.btn_timer_start);
        Button btn_timer_pause = holder.View.findViewById(R.id.btn_timer_pause);
        Button btn_timer_reset = holder.View.findViewById(R.id.btn_timer_reset);
        Button btn_timer_delete = holder.View.findViewById(R.id.btn_timer_delete);

        // Set title value
        txtView_title.setText(alarmTimer.Title);

        // Click listener
        btn_timer_start.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifyAlarmTimerStart(alarmTimer.ID);
            }
        });
        btn_timer_pause.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifyAlarmTimerPause(alarmTimer.ID);
            }
        });
        btn_timer_reset.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifyAlarmTimerReset(alarmTimer.ID);
            }
        });
        btn_timer_delete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifyAlarmTimerDelete(alarmTimer.ID);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Dataset_alarmTimers.size();
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
    public void NotifyAlarmTimerStart(int alarmTimerID) {
        for (IAlarmTimerClickObserver observer : ObservableItemClickedList) {
            observer.OnStart(alarmTimerID);
        }
    }

    @Override
    public void NotifyAlarmTimerPause(int alarmTimerID) {
        for (IAlarmTimerClickObserver observer : ObservableItemClickedList) {
            observer.OnPause(alarmTimerID);
        }
    }

    @Override
    public void NotifyAlarmTimerReset(int alarmTimerID) {
        for (IAlarmTimerClickObserver observer : ObservableItemClickedList) {
            observer.OnReset(alarmTimerID);
        }
    }

    @Override
    public void NotifyAlarmTimerDelete(int alarmTimerID) {
        for (IAlarmTimerClickObserver observer : ObservableItemClickedList) {
            observer.OnDelete(alarmTimerID);
        }
    }
    //#endregion
}
