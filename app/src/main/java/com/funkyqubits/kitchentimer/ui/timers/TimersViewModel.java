package com.funkyqubits.kitchentimer.ui.timers;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerCompleteObserver;
import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.funkyqubits.kitchentimer.Repositories.IRepository;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class TimersViewModel extends ViewModel implements IAlarmTimerCompleteObserver {

    public MutableLiveData<ArrayList<AlarmTimer>> ObservableAlarmTimers = new MutableLiveData<>();
    private ArrayList<AlarmTimer> AlarmTimers = new ArrayList<>();
    private ArrayList<AlarmTimer> RunningTimers = new ArrayList<>();
    private IRepository TimerRepository;

    public TimersViewModel() {
        InitTimer();
    }

    // TODO: Figure out how to use dependency injection in Android MVVM
    public void ProvideRepository(IRepository _timerRepository) {
        this.TimerRepository = _timerRepository;
        AlarmTimers.addAll(TimerRepository.LoadAlarmTimers());

        // Listen for AlarmTimer events
        for (AlarmTimer alarmTimer : AlarmTimers) {
            alarmTimer.RegisterObserver(this);
        }

        ObservableAlarmTimers.setValue(AlarmTimers);
    }

    public ArrayList<AlarmTimer> GetRunningTimers(){
        return RunningTimers;
    }

    public void SaveAllTimersToStorage() {
        TimerRepository.SaveAlarmTimers(AlarmTimers);
    }

    public void SetInitialTimerValues(Map<String, Long> alarmTimers) {
        for (Map.Entry<String, Long> alarmTimerEntry : alarmTimers.entrySet()) {
            UUID id = UUID.fromString(alarmTimerEntry.getKey());
            Long whenTimerBegun = alarmTimerEntry.getValue();

            StartTimer(id, whenTimerBegun);
        }
    }

    public void StartTimer(UUID id, long whenTimerBegun) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.Start(whenTimerBegun);
    }

    public void StartTimer(UUID id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.Start();
    }

    public void PauseTimer(UUID id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.Pause();
        RunningTimers.remove(alarmTimer);
    }

    public void ResetTimer(UUID id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.Reset();
        RunningTimers.remove(alarmTimer);
    }

    private AlarmTimer FindTimerOnId(UUID id) {
        for (AlarmTimer alarmTimer : AlarmTimers) {
            if (alarmTimer.ID.equals(id)) {
                return alarmTimer;
            }
        }
        return null;
    }

    private void InitTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                for (AlarmTimer alarmTimer : RunningTimers) {
                    alarmTimer.Tick();
                }
            }
        }, 0, 1000);//Update every second
    }

    //#region Events for each alarm timer
    @Override
    public void OnAlarmTimerStarted(UUID alarmTimerID) {
        AlarmTimer alarmTimer = FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }

        RunningTimers.add(alarmTimer);
    }

    @Override
    public void OnAlarmTimerResumed(UUID alarmTimerID) {
        AlarmTimer alarmTimer = FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }

        RunningTimers.add(alarmTimer);
    }

    @Override
    public void OnAlarmTimerPaused(UUID alarmTimerID) {
        AlarmTimer alarmTimer = FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }

        RunningTimers.remove(alarmTimer);
    }

    @Override
    public void OnAlarmTimerReset(UUID alarmTimerID) {
        AlarmTimer alarmTimer = FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }

        RunningTimers.remove(alarmTimer);
    }

    @Override
    public void OnAlarmTimerCompleted(UUID alarmTimerID) {
        AlarmTimer alarmTimer = FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }

        RunningTimers.remove(alarmTimer);
    }
    //#endregion
}