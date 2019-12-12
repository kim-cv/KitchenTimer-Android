package com.funkyqubits.kitchentimer.ui.timers;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.funkyqubits.kitchentimer.Controller.AlarmManagerController;
import com.funkyqubits.kitchentimer.Controller.TimerController;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerCompleteObserver;
import com.funkyqubits.kitchentimer.models.AlarmTimer;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TimersViewModel extends ViewModel implements IAlarmTimerCompleteObserver {

    public MutableLiveData<ArrayList<AlarmTimer>> ObservableAlarmTimers = new MutableLiveData<>();
    private TimerController TimerController;
    private AlarmManagerController AlarmManagerController;

    public TimersViewModel() {
    }

    // TODO: Figure out how to use dependency injection in Android MVVM
    public void ProvideExtra(TimerController _timerController, AlarmManagerController alarmManagerController) {
        TimerController = _timerController;
        AlarmManagerController = alarmManagerController;
        ArrayList<AlarmTimer> tmpAlarmTimers = TimerController.AlarmTimers;
        InitTimer();

        // Listen for AlarmTimer events
        for (AlarmTimer alarmTimer : tmpAlarmTimers) {
            alarmTimer.RegisterObserver(this);
        }

        ObservableAlarmTimers.setValue(tmpAlarmTimers);
    }

    public ArrayList<AlarmTimer> GetRunningTimers() {
        return TimerController.GetRunningTimers();
    }

    public void SaveAllTimersToStorage() {
        TimerController.SaveAllTimersToStorage();
    }

    public void SetInitialTimerValues(Map<String, Long> alarmTimers) {
        TimerController.SetInitialTimerValues(alarmTimers);
    }

    public void StartTimer(int id) {
        TimerController.StartTimer(id);
    }

    public void PauseTimer(int id) {
        TimerController.PauseTimer(id);
    }

    public void ResetTimer(int id) {
        TimerController.ResetTimer(id);
    }

    public void DeleteTimer(int id) {
        AlarmTimer alarmTimer = TimerController.FindTimerOnId(id);

        if (alarmTimer == null) {
            return;
        }
        
        alarmTimer.RemoveObserver(this);
        alarmTimer.Pause();
        alarmTimer.Reset();
        TimerController.DeleteTimer(id);

        // Update UI with new list
        ObservableAlarmTimers.setValue(TimerController.AlarmTimers);
    }

    private void InitTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                for (AlarmTimer alarmTimer : TimerController.AlarmTimers) {
                    if (alarmTimer.AlarmTimerState.getValue() == AlarmTimer.ALARMTIMER_STATE.RUNNING) {
                        alarmTimer.Tick();
                    }
                }
            }
        }, 0, 1000);//Update every second
    }

    //#region Events for each alarm timer
    @Override
    public void OnAlarmTimerStarted(int alarmTimerID) {
        AlarmTimer alarmTimer = TimerController.FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }

        AlarmManagerController.ScheduleAlarm(alarmTimer.ID, alarmTimer.LengthInSeconds);
    }

    @Override
    public void OnAlarmTimerResumed(int alarmTimerID) {
        AlarmTimer alarmTimer = TimerController.FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }
    }

    @Override
    public void OnAlarmTimerPaused(int alarmTimerID) {
        AlarmTimer alarmTimer = TimerController.FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }

        AlarmManagerController.CancelAlarm(alarmTimer.ID);
    }

    @Override
    public void OnAlarmTimerReset(int alarmTimerID) {
        AlarmTimer alarmTimer = TimerController.FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }

        AlarmManagerController.CancelAlarm(alarmTimer.ID);
    }

    @Override
    public void OnAlarmTimerCompleted(int alarmTimerID) {
        AlarmTimer alarmTimer = TimerController.FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }
    }
    //#endregion
}