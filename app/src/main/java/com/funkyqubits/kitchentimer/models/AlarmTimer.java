package com.funkyqubits.kitchentimer.models;

import android.text.format.DateUtils;

import androidx.lifecycle.MutableLiveData;

import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerCompleteObserver;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerCompleteSubject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmTimer implements IAlarmTimerCompleteSubject {

    public ALARMTIMER_STATE AlarmTimerState = ALARMTIMER_STATE.NOT_RUNNING;
    public int ID;
    public String Title;
    public MutableLiveData<String> ReadableTimer = new MutableLiveData<>();
    public int LengthInSeconds;

    public long WhenTimerStartedInSeconds;
    private final List<IAlarmTimerCompleteObserver> ObserversAlarmTimerComplete = new ArrayList<>();

    public enum ALARMTIMER_STATE {
        RUNNING,
        NOT_RUNNING,
        PAUSED,
        COMPLETED
    }

    public AlarmTimer(int _id, String _title, int _lengthInSeconds) {
        ctor(_id, _title, _lengthInSeconds);
    }

    public AlarmTimer(String _title, int _lengthInSeconds) {
        ctor(-1, _title, _lengthInSeconds);
    }

    private void ctor(int _id, String _title, int _lengthInSeconds) {
        this.ID = _id;
        this.Title = _title;
        this.LengthInSeconds = _lengthInSeconds;
        ConvertProgressToReadableTimer();
    }

    public void Start(long whenTimerBegun) {
        if (AlarmTimerState == ALARMTIMER_STATE.RUNNING || AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            return;
        }

        WhenTimerStartedInSeconds = whenTimerBegun;
        StartTimer();
    }

    public void Start() {
        if (AlarmTimerState == ALARMTIMER_STATE.RUNNING || AlarmTimerState == ALARMTIMER_STATE.PAUSED || AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            return;
        }

        WhenTimerStartedInSeconds = Calendar.getInstance().getTimeInMillis() / 1000;
        StartTimer();
    }

    private void StartTimer() {
        AlarmTimerState = ALARMTIMER_STATE.RUNNING;
        NotifyAlarmTimerStarted(ID);
        CalculateIfTimerComplete();
        ConvertProgressToReadableTimer();
    }


    public void Pause() {
        if (AlarmTimerState == ALARMTIMER_STATE.PAUSED || AlarmTimerState == ALARMTIMER_STATE.NOT_RUNNING || AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            return;
        }

        AlarmTimerState = ALARMTIMER_STATE.PAUSED;
        ConvertProgressToReadableTimer();
        NotifyAlarmTimerPaused(ID);
    }

    public void Reset() {
        if (AlarmTimerState == ALARMTIMER_STATE.RUNNING || AlarmTimerState == ALARMTIMER_STATE.NOT_RUNNING) {
            return;
        }

        AlarmTimerState = ALARMTIMER_STATE.NOT_RUNNING;
        WhenTimerStartedInSeconds = 0;
        ConvertProgressToReadableTimer();
        NotifyAlarmTimerReset(ID);
    }

    /**
     * Is called each second
     */
    public void Tick() {
        if (AlarmTimerState != ALARMTIMER_STATE.RUNNING) {
            return;
        }

        ConvertProgressToReadableTimer();

        CalculateIfTimerComplete();
    }

    private void CalculateIfTimerComplete() {
        long timerProgress = CalculateTimerProgress();

        if (timerProgress >= LengthInSeconds) {
            AlarmTimerState = ALARMTIMER_STATE.COMPLETED;
            NotifyAlarmTimerCompleted(ID);
        }
    }

    private Long CalculateTimerProgress() {
        long nowSeconds = Calendar.getInstance().getTimeInMillis() / 1000;
        long timerProgress = nowSeconds - WhenTimerStartedInSeconds;
        return timerProgress;
    }

    private void ConvertProgressToReadableTimer() {
        long progress = CalculateTimerProgress();
        if (AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            this.ReadableTimer.postValue(DateUtils.formatElapsedTime(0));
        } else if (AlarmTimerState == ALARMTIMER_STATE.NOT_RUNNING) {
            this.ReadableTimer.postValue(DateUtils.formatElapsedTime(LengthInSeconds));
        } else {
            this.ReadableTimer.postValue(DateUtils.formatElapsedTime(LengthInSeconds - progress));
        }
    }

    //#region Events
    @Override
    public void RegisterObserver(IAlarmTimerCompleteObserver observer) {
        if (!ObserversAlarmTimerComplete.contains(observer)) {
            ObserversAlarmTimerComplete.add(observer);
        }
    }

    @Override
    public void RemoveObserver(IAlarmTimerCompleteObserver observer) {
        if (ObserversAlarmTimerComplete.contains(observer)) {
            ObserversAlarmTimerComplete.remove(observer);
        }
    }

    @Override
    public void NotifyAlarmTimerStarted(int alarmTimerID) {
        for (IAlarmTimerCompleteObserver observer : ObserversAlarmTimerComplete) {
            observer.OnAlarmTimerStarted(alarmTimerID);
        }
    }

    @Override
    public void NotifyAlarmTimerResumed(int alarmTimerID) {
        for (IAlarmTimerCompleteObserver observer : ObserversAlarmTimerComplete) {
            observer.OnAlarmTimerResumed(alarmTimerID);
        }
    }

    @Override
    public void NotifyAlarmTimerPaused(int alarmTimerID) {
        for (IAlarmTimerCompleteObserver observer : ObserversAlarmTimerComplete) {
            observer.OnAlarmTimerPaused(alarmTimerID);
        }
    }

    @Override
    public void NotifyAlarmTimerReset(int alarmTimerID) {
        for (IAlarmTimerCompleteObserver observer : ObserversAlarmTimerComplete) {
            observer.OnAlarmTimerReset(alarmTimerID);
        }
    }

    @Override
    public void NotifyAlarmTimerCompleted(int alarmTimerID) {
        for (IAlarmTimerCompleteObserver observer : ObserversAlarmTimerComplete) {
            observer.OnAlarmTimerCompleted(alarmTimerID);
        }
    }
    //#endregion
}
