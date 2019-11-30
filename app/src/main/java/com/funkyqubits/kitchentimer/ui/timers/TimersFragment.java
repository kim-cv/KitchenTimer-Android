package com.funkyqubits.kitchentimer.ui.timers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerClickObserver;
import com.funkyqubits.kitchentimer.Models.AlarmTimer;
import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.Repositories.FileSystemRepository;
import com.funkyqubits.kitchentimer.Repositories.IRepository;

import java.util.ArrayList;
import java.util.UUID;

public class TimersFragment extends Fragment implements IAlarmTimerClickObserver {

    private RecyclerView recyclerView;
    private AlarmTimersAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    private TimersViewModel timersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        timersViewModel =
                ViewModelProviders.of(this).get(TimersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_timers, container, false);


        // TODO: Figure out how to use dependency injection in Android MVVM
        IRepository repository = new FileSystemRepository(getContext(), "SavedTimings.json");
        timersViewModel.ProvideRepository(repository);


        recyclerView = root.findViewById(R.id.recyclerview_alarmTimers);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new AlarmTimersAdapter();
        mAdapter.RegisterObserver(this);

        // Listen for when timers are ready and then give them to the adapter
        timersViewModel.ObservableAlarmTimers.observe(this, new Observer<ArrayList<AlarmTimer>>() {
                    @Override
                    public void onChanged(ArrayList<AlarmTimer> alarmTimers) {
                        mAdapter.SetData(alarmTimers);
                        recyclerView.setAdapter(mAdapter);
                    }
                }
        );

        return root;
    }


    //#region Timer UI events
    @Override
    public void OnStart(UUID alarmTimerID) {
        timersViewModel.StartTimer(alarmTimerID);
    }

    @Override
    public void OnPause(UUID alarmTimerID) {
        timersViewModel.PauseTimer(alarmTimerID);
    }

    @Override
    public void OnReset(UUID alarmTimerID) {
        timersViewModel.ResetTimer(alarmTimerID);
    }
    //#endregion
}