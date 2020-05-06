package com.funkyqubits.kitchentimer.ui.timers;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.funkyqubits.kitchentimer.Controller.AlarmManagerController;
import com.funkyqubits.kitchentimer.Controller.TimerController;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerObserver;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerUIEventsObserver;
import com.funkyqubits.kitchentimer.Repositories.FileSystemRepository;
import com.funkyqubits.kitchentimer.Repositories.ISharedPreferencesRepository;
import com.funkyqubits.kitchentimer.Repositories.SharedPreferencesRepository;
import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.Repositories.IFileSystemRepository;
import com.funkyqubits.kitchentimer.services.AlarmAudioService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class TimersFragment extends Fragment implements IAlarmTimerUIEventsObserver, IAlarmTimerObserver {

    private RecyclerView RecyclerView;
    private AlarmTimersAdapter RecyclerViewAdapter;
    private RecyclerView.LayoutManager LayoutManager;


    private TimersViewModel TimersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TimersViewModel = new ViewModelProvider(this).get(TimersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_timers, container, false);


        // TODO: Figure out how to use dependency injection in Android MVVM
        IFileSystemRepository repository = new FileSystemRepository(getContext(), getString(R.string.file_timers));
        ISharedPreferencesRepository timerOffsets = new SharedPreferencesRepository(getContext());
        AlarmManagerController alarmManagerController = new AlarmManagerController(getContext());
        TimerController timerController = TimerController.Instance(repository, timerOffsets);
        TimersViewModel.ProvideExtra(timerController, alarmManagerController);


        RecyclerView = root.findViewById(R.id.recyclerview_alarmTimers);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        RecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.setLayoutManager(LayoutManager);

        RecyclerViewAdapter = new AlarmTimersAdapter(this);

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

        RecyclerViewAdapter.RegisterObserver(this);
        TimersViewModel.AddObserverToAlarmTimers(this);

        AlarmAudioService.Companion.timersInFocus(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();

        RecyclerViewAdapter.RemoveObserver(this);
        TimersViewModel.RemoveObserverFromAlarmTimers(this);

        // Save timers to storage
        TimersViewModel.SaveAllTimersToStorage();
    }

    //#region Timer UI events
    @Override
    public void OnUIAlarmTimerStart(int alarmTimerID) {
        TimersViewModel.StartTimer(alarmTimerID);
    }

    @Override
    public void OnUIAlarmTimerPause(int alarmTimerID) {
        TimersViewModel.PauseTimer(alarmTimerID);
    }

    @Override
    public void OnUIAlarmTimerReset(int alarmTimerID) {
        TimersViewModel.ResetTimer(alarmTimerID);
    }

    @Override
    public void OnUIAlarmTimerEdit(int alarmTimerID) {
        NavController navController = Navigation.findNavController(this.getView());
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.parameter_timerId), alarmTimerID);
        navController.navigate(R.id.navigation_add_timer, bundle);
    }

    @Override
    public void OnUIAlarmTimerDelete(final int alarmTimerID) {
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Are you sure")
                .setMessage("You want to remove this timer?")
                .setPositiveButton("Yes Remove", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TimersViewModel.DeleteTimer(alarmTimerID);
                    }
                })
                .setNeutralButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .show();
    }
    //#endregion

    //#region Events for each alarm timer
    @Override
    public void OnAlarmTimerStarted(int alarmTimerID) {
    }

    @Override
    public void OnAlarmTimerResumed(int alarmTimerID) {
    }

    @Override
    public void OnAlarmTimerPaused(int alarmTimerID) {
    }

    @Override
    public void OnAlarmTimerReset(int alarmTimerID) {
        AlarmAudioService.Companion.timersInFocus(getContext());
    }

    @Override
    public void OnAlarmTimerCompleted(int alarmTimerID) {

    }
    //#endregion
}