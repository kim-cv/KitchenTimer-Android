package com.funkyqubits.kitchentimer.ui.timers;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.funkyqubits.kitchentimer.Controller.AlarmManagerController;
import com.funkyqubits.kitchentimer.Controller.TimerController;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerObserver;
import com.funkyqubits.kitchentimer.models.AlarmTimer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TimersViewModel extends ViewModel implements IAlarmTimerObserver {

    public MutableLiveData<ArrayList<AlarmTimer>> ObservableAlarmTimers = new MutableLiveData<>();
    private TimerController TimerController;
    private AlarmManagerController AlarmManagerController;
    private Timer Timer = new Timer();

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d("DebugService", "TimersViewModel onCleared:");
        Timer.cancel();
        Timer = null;
    }

    public TimersViewModel(TimerController _timerController, AlarmManagerController alarmManagerController) {
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

    //#region Observing
    public void AddObserverToAlarmTimers(IAlarmTimerObserver observer) {
        for (AlarmTimer alarmTimer : TimerController.AlarmTimers) {
            alarmTimer.RegisterObserver(observer);
        }
    }

    public void RemoveObserverFromAlarmTimers(IAlarmTimerObserver observer) {
        for (AlarmTimer alarmTimer : TimerController.AlarmTimers) {
            alarmTimer.RemoveObserver(observer);
        }
    }
    //#endregion

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

    public void SaveAllTimersToStorage() {
        TimerController.SaveAllTimersToStorage();
    }

    private void InitTimer() {
        Log.d("DebugService", "Init Timer Ticking");
        /*
        Very bad code!
        Problem: Timer was running on separate background thread even after viewmodel was destroyed, if viewmodel was recreated another background timer thread
        would also be started, meaning it would leak background threads until the app was stopped.
        First solution: Cleanup timer on overridden method onCleared but that method is not always called :(
        Temporary quick fix: If timer background thread is running, stop it and recreate a new timer. Bad but works :)
         */
        if (Timer != null) {
            Timer.cancel();
            Timer = null;
        }

        Timer = new Timer();
        Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (AlarmTimer alarmTimer : TimerController.AlarmTimers) {
                    if (alarmTimer.AlarmTimerState == AlarmTimer.ALARMTIMER_STATE.RUNNING) {
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

        AlarmManagerController.ScheduleAlarm(alarmTimer, (int) alarmTimer.CalculateRemainingSeconds());
    }

    @Override
    public void OnAlarmTimerResumed(int alarmTimerID) {
        AlarmTimer alarmTimer = TimerController.FindTimerOnId(alarmTimerID);
        if (alarmTimer == null) {
            return;
        }

        AlarmManagerController.ScheduleAlarm(alarmTimer, (int) alarmTimer.CalculateRemainingSeconds());
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
    }
    //#endregion
}