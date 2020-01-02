package com.funkyqubits.kitchentimer.ui.timers;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.funkyqubits.kitchentimer.Controller.AlarmManagerController;
import com.funkyqubits.kitchentimer.Controller.TimerController;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerUIEventsObserver;
import com.funkyqubits.kitchentimer.Repositories.ISharedPreferencesRepository;
import com.funkyqubits.kitchentimer.Repositories.SharedPreferencesRepository;
import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.Repositories.FileSystemRepository;
import com.funkyqubits.kitchentimer.Repositories.IFileSystemRepository;
import com.funkyqubits.kitchentimer.models.AlarmTimerOffset;
import com.funkyqubits.kitchentimer.services.AlarmAudioService;

import java.util.ArrayList;

public class TimersFragment extends Fragment implements IAlarmTimerUIEventsObserver {

    private RecyclerView RecyclerView;
    private AlarmTimersAdapter RecyclerViewAdapter;
    private RecyclerView.LayoutManager LayoutManager;


    private TimersViewModel TimersViewModel;
    private ISharedPreferencesRepository SharedPreferencesRepository;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TimersViewModel =
                ViewModelProviders.of(this).get(TimersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_timers, container, false);


        // TODO: Figure out how to use dependency injection in Android MVVM
        IFileSystemRepository repository = new FileSystemRepository(getContext(), getString(R.string.file_timers));
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
        // Load timer data from shared preferences
        SharedPreferencesRepository = new SharedPreferencesRepository(getContext());
        ArrayList<AlarmTimerOffset> timerOffsets = SharedPreferencesRepository.GetOffsets();
        TimersViewModel.SetTimerOffsets(timerOffsets);
        AlarmAudioService.Companion.timersInFocus(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();

        //Save running timers to shared preferences key/value storage
        ArrayList<AlarmTimer> runningAlarmTimers = TimersViewModel.GetRunningTimers();
        SharedPreferencesRepository.SaveRunningTimersStartOffset(runningAlarmTimers);
        SharedPreferencesRepository.SaveRunningTimersPauseOffsets(runningAlarmTimers);

        // Save timers to storage
        TimersViewModel.SaveAllTimersToStorage();
    }

    //#region Timer UI events
    @Override
    public void OnStart(int alarmTimerID) {
        TimersViewModel.StartTimer(alarmTimerID);
        AlarmAudioService.Companion.startService(getContext());
    }

    @Override
    public void OnPause(int alarmTimerID) {
        TimersViewModel.PauseTimer(alarmTimerID);
    }

    @Override
    public void OnReset(int alarmTimerID) {
        TimersViewModel.ResetTimer(alarmTimerID);
        AlarmAudioService.Companion.timersInFocus(getContext());
    }

    @Override
    public void OnEdit(int alarmTimerID) {
        NavController navController = Navigation.findNavController(this.getView());
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.parameter_timerId), alarmTimerID);
        navController.navigate(R.id.navigation_add_timer, bundle);
    }

    @Override
    public void OnDelete(final int alarmTimerID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);

        builder.setTitle("Are you sure");
        builder.setMessage("You want to remove this timer?");

        builder.setPositiveButton("Yes Remove", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                TimersViewModel.DeleteTimer(alarmTimerID);
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
    //#endregion
}