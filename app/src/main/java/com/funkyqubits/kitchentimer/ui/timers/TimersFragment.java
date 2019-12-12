package com.funkyqubits.kitchentimer.ui.timers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.funkyqubits.kitchentimer.Controller.AlarmManagerController;
import com.funkyqubits.kitchentimer.Controller.TimerController;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerClickObserver;
import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.Repositories.FileSystemRepository;
import com.funkyqubits.kitchentimer.Repositories.IRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimersFragment extends NavHostFragment implements IAlarmTimerClickObserver {

    private RecyclerView RecyclerView;
    private AlarmTimersAdapter RecyclerViewAdapter;
    private RecyclerView.LayoutManager LayoutManager;


    private TimersViewModel TimersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TimersViewModel =
                ViewModelProviders.of(this).get(TimersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_timers, container, false);


        // TODO: Figure out how to use dependency injection in Android MVVM
        IRepository repository = new FileSystemRepository(getContext(), getString(R.string.file_timers));
        AlarmManagerController alarmManagerController = new AlarmManagerController(getContext());
        TimerController timerController = TimerController.Instance(repository);
        TimersViewModel.ProvideExtra(timerController, alarmManagerController);


        RecyclerView = root.findViewById(R.id.recyclerview_alarmTimers);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        RecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.setLayoutManager(LayoutManager);

        RecyclerViewAdapter = new AlarmTimersAdapter(this);
        RecyclerViewAdapter.RegisterObserver(this);

        // Listen for when timers are ready and then give them to the adapter
        TimersViewModel.ObservableAlarmTimers.observe(getViewLifecycleOwner(), new Observer<ArrayList<AlarmTimer>>() {
                    @Override
                    public void onChanged(ArrayList<AlarmTimer> alarmTimers) {
                        RecyclerViewAdapter.SetData(alarmTimers);
                        RecyclerView.setAdapter(RecyclerViewAdapter);
                    }
                }
        );

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        Map<String, Long> runningTimersData = new HashMap<>();

        // Get Context and SharedPreferences
        Context context = getContext();
        String filename = getString(R.string.preference_file_runningTimers);
        SharedPreferences sharedPreferences_runningTimers = context.getSharedPreferences(filename, Context.MODE_PRIVATE);

        // Map sharedPreference data
        Map<String, ?> allEntries = sharedPreferences_runningTimers.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Long parseLong = Long.parseLong(entry.getValue().toString());
            runningTimersData.put(entry.getKey(), parseLong);
        }

        TimersViewModel.SetInitialTimerValues(runningTimersData);
    }

    @Override
    public void onPause() {
        super.onPause();

        //#region Save running timers to shared preferences key/value storage
        ArrayList<AlarmTimer> runningAlarmTimers = TimersViewModel.GetRunningTimers();

        // Get Context and SharedPreferences
        Context context = getContext();
        String filename = getString(R.string.preference_file_runningTimers);
        SharedPreferences sharedPreferences_runningTimers = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferences_editor = sharedPreferences_runningTimers.edit();

        // Commit timer data
        sharedPreferences_editor.clear();
        for (AlarmTimer alarmTimer : runningAlarmTimers) {
            sharedPreferences_editor.putLong(Integer.toString(alarmTimer.ID), alarmTimer.WhenTimerStartedInSeconds);
        }
        sharedPreferences_editor.commit();
        //#endregion


        // Save timers to storage
        TimersViewModel.SaveAllTimersToStorage();
    }

    //#region Timer UI events
    @Override
    public void OnStart(int alarmTimerID) {
        TimersViewModel.StartTimer(alarmTimerID);
    }

    @Override
    public void OnPause(int alarmTimerID) {
        TimersViewModel.PauseTimer(alarmTimerID);
    }

    @Override
    public void OnReset(int alarmTimerID) {
        TimersViewModel.ResetTimer(alarmTimerID);
    }

    @Override
    public void OnDelete(final int alarmTimerID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Are you sure?");
        builder.setMessage("Are you sure you want to delete this timer?");

        builder.setPositiveButton("Yes Remove", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                TimersViewModel.DeleteTimer(alarmTimerID);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //#endregion
}