package com.funkyqubits.kitchentimer.ui.timers;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.funkyqubits.kitchentimer.Controller.AlarmManagerController;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerCompleteObserver;
import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.funkyqubits.kitchentimer.Repositories.IRepository;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TimersViewModel extends ViewModel implements IAlarmTimerCompleteObserver {

    public MutableLiveData<ArrayList<AlarmTimer>> ObservableAlarmTimers = new MutableLiveData<>();
    private ArrayList<AlarmTimer> AlarmTimers = new ArrayList<>();
    private ArrayList<AlarmTimer> RunningTimers = new ArrayList<>();
    private IRepository TimerRepository;
    private AlarmManagerController AlarmManagerController;

    public TimersViewModel() {
        InitTimer();
    }

    // TODO: Figure out how to use dependency injection in Android MVVM
    public void ProvideExtra(IRepository _timerRepository, AlarmManagerController alarmManagerController) {
        this.TimerRepository = _timerRepository;
        AlarmManagerController = alarmManagerController;

        /*
            Old app didn't use unique ID's so for backward compatibility loading old timers from storage receive -1 as id
            When timers are loaded this piece of code check for timers with -1 as id and assign a new unique id
        */
        ArrayList<AlarmTimer> tmpAlarmTimers = TimerRepository.LoadAlarmTimers();
        for (AlarmTimer tmpAlarmTimer : tmpAlarmTimers) {
            if (tmpAlarmTimer.ID == -1) {
                int newRandomId = GenerateUniqueIntId();
                tmpAlarmTimer.ID = newRandomId;
            }
        }

        AlarmTimers.addAll(tmpAlarmTimers);

        // Listen for AlarmTimer events
        for (AlarmTimer alarmTimer : AlarmTimers) {
            alarmTimer.RegisterObserver(this);
        }

        ObservableAlarmTimers.setValue(AlarmTimers);
    }

    public ArrayList<AlarmTimer> GetRunningTimers() {
        return RunningTimers;
    }

    public void SaveAllTimersToStorage() {
        TimerRepository.SaveAlarmTimers(AlarmTimers);
    }

    public void SetInitialTimerValues(Map<String, Long> alarmTimers) {
        for (Map.Entry<String, Long> alarmTimerEntry : alarmTimers.entrySet()) {
            int id = Integer.parseInt(alarmTimerEntry.getKey());
            Long whenTimerBegun = alarmTimerEntry.getValue();

            StartTimer(id, whenTimerBegun);
        }
    }

    public void StartTimer(int id, long whenTimerBegun) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.Start(whenTimerBegun);
    }

    public void StartTimer(int id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.Start();
    }

    public void PauseTimer(int id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.Pause();
        RunningTimers.remove(alarmTimer);
    }

    public void ResetTimer(int id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.Reset();
        RunningTimers.remove(alarmTimer);
    }

    private AlarmTimer FindTimerOnId(int id) {
        for (AlarmTimer alarmTimer : AlarmTimers) {
            if (alarmTimer.ID == id) {
                return alarmTimer;
            }
        }
        return null;
    }

    private int GenerateUniqueIntId() {
        int randomId = (int) (Math.random() * ((99999999 - 1) + 1)) + 1;
        AlarmTimer tmpAlarmTimer = FindTimerOnId(randomId);

        if (tmpAlarmTimer == null) {
            return randomId;
        } else {
            return GenerateUniqueIntId();
        }
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
    public void OnAlarmTimerStarted(int alarmTimerID) {
        AlarmTimer alarmTimer = FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }

        AlarmManagerController.ScheduleAlarm(alarmTimer.ID, alarmTimer.LengthInSeconds);
        RunningTimers.add(alarmTimer);
    }

    @Override
    public void OnAlarmTimerResumed(int alarmTimerID) {
        AlarmTimer alarmTimer = FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }

        RunningTimers.add(alarmTimer);
    }

    @Override
    public void OnAlarmTimerPaused(int alarmTimerID) {
        AlarmTimer alarmTimer = FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }

        AlarmManagerController.CancelAlarm(alarmTimer.ID);
        RunningTimers.remove(alarmTimer);
    }

    @Override
    public void OnAlarmTimerReset(int alarmTimerID) {
        AlarmTimer alarmTimer = FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }

        AlarmManagerController.CancelAlarm(alarmTimer.ID);
        RunningTimers.remove(alarmTimer);
    }

    @Override
    public void OnAlarmTimerCompleted(int alarmTimerID) {
        AlarmTimer alarmTimer = FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }

        //AlarmManagerController.CancelAlarm(alarmTimer.ID);
        RunningTimers.remove(alarmTimer);
    }
    //#endregion
}