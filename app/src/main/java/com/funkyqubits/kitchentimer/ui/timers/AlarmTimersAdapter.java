package com.funkyqubits.kitchentimer.ui.timers;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.SortedListAdapterCallback;

import com.funkyqubits.kitchentimer.BR;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerUIEventsObserver;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerUIEventsSubject;
import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.funkyqubits.kitchentimer.R;

import java.util.ArrayList;
import java.util.List;

public class AlarmTimersAdapter extends RecyclerView.Adapter<AlarmTimersAdapter.ViewHolder> implements IAlarmTimerUIEventsSubject {
    private final List<IAlarmTimerUIEventsObserver> AlarmTimerUIEventsObservers = new ArrayList<>();
    private SortedList<AlarmTimer> Sorted_Dataset_alarmTimers;
    private Fragment ContainingFragment;

    public void UpdateItemPosition(int alarmTimerID) {
        for (int i = 0; i <= Sorted_Dataset_alarmTimers.size(); i++) {
            if (Sorted_Dataset_alarmTimers.get(i).ID == alarmTimerID) {
                Sorted_Dataset_alarmTimers.recalculatePositionOfItemAt(i);
                break;
            }
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View View;
        public int AlarmTimerID;
        public AlarmTimer AlarmTimer;
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

        this.Sorted_Dataset_alarmTimers = new SortedList<>(AlarmTimer.class, new SortedListAdapterCallback<AlarmTimer>(this) {
            @Override
            public int compare(AlarmTimer item1, AlarmTimer item2) {
                // Either sort by state priority or alphabetically
                int result = item1.AlarmTimerState.priority - item2.AlarmTimerState.priority;

                // If their priority is equal, sort by title
                if (result == 0) {
                    return item1.Title.compareToIgnoreCase(item2.Title);
                }

                return result;
                /*
                    0: if (x==y)
                    -1: if (x < y)
                    1: if (x > y)
                */
            }

            @Override
            public boolean areContentsTheSame(AlarmTimer oldItem, AlarmTimer newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(AlarmTimer item1, AlarmTimer item2) {
                return item1 == item2;
            }
        });
    }

    public void SetData(ArrayList<AlarmTimer> alarmTimers) {
        Sorted_Dataset_alarmTimers.clear();
        Sorted_Dataset_alarmTimers.addAll(alarmTimers);
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final AlarmTimer alarmTimer = Sorted_Dataset_alarmTimers.get(position);

        // Bind model to view
        holder.binding.setVariable(BR.alarmTimer, alarmTimer);
        holder.binding.executePendingBindings();

        // Set variables onto the view holder
        holder.AlarmTimerID = alarmTimer.ID;
        holder.AlarmTimer = alarmTimer;

        // Find views
        TextView txtView_title = holder.View.findViewById(R.id.timer_title);
        /*
        Button btn_timer_start = holder.View.findViewById(R.id.btn_timer_start);
        Button btn_timer_pause = holder.View.findViewById(R.id.btn_timer_pause);
        Button btn_timer_reset = holder.View.findViewById(R.id.btn_timer_reset);
        Button btn_timer_edit = holder.View.findViewById(R.id.btn_timer_edit);
        Button btn_timer_delete = holder.View.findViewById(R.id.btn_timer_delete);
         */

        // Set title value
        txtView_title.setText(alarmTimer.Title);

        // Click listener
        holder.View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarmTimer.AlarmTimerState == AlarmTimer.ALARMTIMER_STATE.NOT_RUNNING || alarmTimer.AlarmTimerState == AlarmTimer.ALARMTIMER_STATE.PAUSED) {
                    NotifyOfUIAlarmTimerStart(alarmTimer.ID);
                } else {
                    NotifyOfUIAlarmTimerPause(alarmTimer.ID);
                }
            }
        });
        holder.View.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {

                // Create options dialog
                TimerOptionsDialog dialog = new TimerOptionsDialog(holder.AlarmTimer, ContainingFragment);
                dialog.show(ContainingFragment.getParentFragmentManager(), Integer.toString(holder.AlarmTimerID));

                return true;
            }
        });

        /*
        btn_timer_start.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifyOfUIAlarmTimerStart(alarmTimer.ID);
            }
        });
        btn_timer_pause.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifyOfUIAlarmTimerPause(alarmTimer.ID);
            }
        });
        btn_timer_reset.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifyOfUIAlarmTimerReset(alarmTimer.ID);
            }
        });
        btn_timer_edit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifyOfUIAlarmTimerEdit(alarmTimer.ID);
            }
        });
        btn_timer_delete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifyOfUIAlarmTimerDelete(alarmTimer.ID);
            }
        });
        */
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Sorted_Dataset_alarmTimers.size();
    }

    //#region Subject/Observer
    @Override
    public void RegisterObserver(IAlarmTimerUIEventsObserver observer) {
        if (!AlarmTimerUIEventsObservers.contains(observer)) {
            AlarmTimerUIEventsObservers.add(observer);
        }
    }

    @Override
    public void RemoveObserver(IAlarmTimerUIEventsObserver observer) {
        if (AlarmTimerUIEventsObservers.contains(observer)) {
            AlarmTimerUIEventsObservers.remove(observer);
        }
    }

    @Override
    public void NotifyOfUIAlarmTimerStart(int alarmTimerID) {
        for (IAlarmTimerUIEventsObserver observer : AlarmTimerUIEventsObservers) {
            observer.OnUIAlarmTimerStart(alarmTimerID);
        }
    }

    @Override
    public void NotifyOfUIAlarmTimerPause(int alarmTimerID) {
        for (IAlarmTimerUIEventsObserver observer : AlarmTimerUIEventsObservers) {
            observer.OnUIAlarmTimerPause(alarmTimerID);
        }
    }

    @Override
    public void NotifyOfUIAlarmTimerReset(int alarmTimerID) {
        for (IAlarmTimerUIEventsObserver observer : AlarmTimerUIEventsObservers) {
            observer.OnUIAlarmTimerReset(alarmTimerID);
        }
    }

    @Override
    public void NotifyOfUIAlarmTimerEdit(int alarmTimerID) {
        for (IAlarmTimerUIEventsObserver observer : AlarmTimerUIEventsObservers) {
            observer.OnUIAlarmTimerEdit(alarmTimerID);
        }
    }

    @Override
    public void NotifyOfUIAlarmTimerDelete(int alarmTimerID) {
        for (IAlarmTimerUIEventsObserver observer : AlarmTimerUIEventsObservers) {
            observer.OnUIAlarmTimerDelete(alarmTimerID);
        }
    }
    //#endregion
}
