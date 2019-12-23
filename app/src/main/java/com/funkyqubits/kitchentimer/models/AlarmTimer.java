package com.funkyqubits.kitchentimer.models;

import android.text.format.DateUtils;

import androidx.lifecycle.MutableLiveData;

import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerCompleteObserver;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerCompleteSubject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmTimer implements IAlarmTimerCompleteSubject {

    public MutableLiveData<ALARMTIMER_STATE> ObservableAlarmTimerState = new MutableLiveData<>();
    public ALARMTIMER_STATE AlarmTimerState;
    public int ID;
    public String Title;
    public MutableLiveData<String> ReadableTimer = new MutableLiveData<>();
    public int LengthInSeconds;
    public ALARMTIMER_SAVE_TYPE SaveType;

    public long WhenTimerStartedInSeconds;
    private final List<IAlarmTimerCompleteObserver> ObserversAlarmTimerComplete = new ArrayList<>();

    // Used for calculating time spent "PAUSED" as an offset for the timer progress
    public long ResumeSecondsOffset;
    private long StartedPause;

    public enum ALARMTIMER_STATE {
        RUNNING,
        NOT_RUNNING,
        PAUSED,
        COMPLETED
    }

    public enum ALARMTIMER_SAVE_TYPE {
        SAVE,
        SINGLE_USE
    }

    public AlarmTimer(int _id, String _title, int _lengthInSeconds, ALARMTIMER_SAVE_TYPE _saveType) {
        ctor(_id, _title, _lengthInSeconds, _saveType);
    }

    public AlarmTimer(String _title, int _lengthInSeconds, ALARMTIMER_SAVE_TYPE _saveType) {
        ctor(-1, _title, _lengthInSeconds, _saveType);
    }

    private void ctor(int _id, String _title, int _lengthInSeconds, ALARMTIMER_SAVE_TYPE _saveType) {
        this.ObservableAlarmTimerState.setValue(ALARMTIMER_STATE.NOT_RUNNING);
        this.AlarmTimerState = ALARMTIMER_STATE.NOT_RUNNING;

        this.ID = _id;
        this.Title = _title;
        this.LengthInSeconds = _lengthInSeconds;
        this.SaveType = _saveType;
        ConvertProgressToReadableTimer();
    }

    public void SetOffset(AlarmTimerOffset offset) {
        if (AlarmTimerState == ALARMTIMER_STATE.RUNNING || AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            return;
        }

        WhenTimerStartedInSeconds = offset.SecondsStartOffset;
        ResumeSecondsOffset = offset.SecondsPauseOffset;
        if (!CalculateIfTimerComplete()) {
            StartTimer();
        }
        ConvertProgressToReadableTimer();
    }

    public void Start() {
        if (AlarmTimerState == ALARMTIMER_STATE.RUNNING || AlarmTimerState == ALARMTIMER_STATE.PAUSED || AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            return;
        }

        WhenTimerStartedInSeconds = GetNowSeconds();
        StartTimer();
    }

    private void StartTimer() {
        if (!CalculateIfTimerComplete()) {
            ObservableAlarmTimerState.setValue(ALARMTIMER_STATE.RUNNING);
            AlarmTimerState = ALARMTIMER_STATE.RUNNING;
            NotifyAlarmTimerStarted(ID);
        }
        ConvertProgressToReadableTimer();
    }


    public void Pause() {
        if (AlarmTimerState == ALARMTIMER_STATE.PAUSED || AlarmTimerState == ALARMTIMER_STATE.NOT_RUNNING || AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            return;
        }

        ObservableAlarmTimerState.setValue(ALARMTIMER_STATE.PAUSED);
        AlarmTimerState = ALARMTIMER_STATE.PAUSED;
        ConvertProgressToReadableTimer();
        StartedPause = GetNowSeconds();
        NotifyAlarmTimerPaused(ID);
    }

    public void Resume() {
        if (AlarmTimerState == ALARMTIMER_STATE.RUNNING || AlarmTimerState == ALARMTIMER_STATE.NOT_RUNNING || AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            return;
        }
        long now = GetNowSeconds();
        ResumeSecondsOffset += now - StartedPause;
        StartedPause = 0;

        ObservableAlarmTimerState.setValue(ALARMTIMER_STATE.RUNNING);
        AlarmTimerState = ALARMTIMER_STATE.RUNNING;
        ConvertProgressToReadableTimer();
        NotifyAlarmTimerResumed(ID);
    }

    public void Reset() {
        if (AlarmTimerState == ALARMTIMER_STATE.RUNNING || AlarmTimerState == ALARMTIMER_STATE.NOT_RUNNING) {
            return;
        }

        ObservableAlarmTimerState.setValue(ALARMTIMER_STATE.NOT_RUNNING);
        AlarmTimerState = ALARMTIMER_STATE.NOT_RUNNING;
        WhenTimerStartedInSeconds = 0;
        ResumeSecondsOffset = 0;
        StartedPause = 0;
        ConvertProgressToReadableTimer();
        NotifyAlarmTimerReset(ID);
    }

    public int GetNowSeconds() {
        return (int) Math.floor(Calendar.getInstance().getTimeInMillis() / 1000);
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

    private boolean CalculateIfTimerComplete() {
        long timerProgress = CalculateTimerProgress();

        if (timerProgress >= LengthInSeconds) {
            ObservableAlarmTimerState.postValue(ALARMTIMER_STATE.COMPLETED);
            AlarmTimerState = ALARMTIMER_STATE.COMPLETED;
            NotifyAlarmTimerCompleted(ID);
        }

        return timerProgress >= LengthInSeconds;
    }

    public long CalculateTimerProgress() {
        long nowSeconds = GetNowSeconds();
        long timerProgress = nowSeconds - WhenTimerStartedInSeconds - ResumeSecondsOffset;
        return timerProgress;
    }

    public long CalculateRemainingSeconds() {
        return LengthInSeconds - CalculateTimerProgress();
    }

    private void ConvertProgressToReadableTimer() {
        if (AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            this.ReadableTimer.postValue(DateUtils.formatElapsedTime(0));
        } else if (AlarmTimerState == ALARMTIMER_STATE.NOT_RUNNING) {
            this.ReadableTimer.postValue(DateUtils.formatElapsedTime(LengthInSeconds));
        } else {
            long remainingSeconds = CalculateRemainingSeconds();
            this.ReadableTimer.postValue(DateUtils.formatElapsedTime(remainingSeconds));
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
