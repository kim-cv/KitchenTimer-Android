package com.funkyqubits.kitchentimer.models;

import android.text.format.DateUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerObserver;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerSubject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmTimer implements IAlarmTimerSubject {

    public MutableLiveData<ALARMTIMER_STATE> ObservableAlarmTimerState = new MutableLiveData<>();
    public ALARMTIMER_STATE AlarmTimerState;
    public int ID;
    public String Title;
    public MutableLiveData<String> ReadableTimer = new MutableLiveData<>();
    public int LengthInSeconds;
    public ALARMTIMER_SAVE_TYPE SaveType;

    public long WhenTimerStartedInSeconds;
    private final List<IAlarmTimerObserver> AlarmTimerObservers = new ArrayList<>();

    // Used for calculating time spent "PAUSED" as an offset for the timer progress
    public long ResumeSecondsOffset;
    private long StartedPause;

    public enum ALARMTIMER_STATE implements Comparable<ALARMTIMER_STATE> {
        COMPLETED(1),
        RUNNING(2),
        PAUSED(3),
        NOT_RUNNING(4);

        public final int priority;

        private ALARMTIMER_STATE(int priority) {
            this.priority = priority;
        }
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
        //this.ObservableAlarmTimerState.setValue(ALARMTIMER_STATE.NOT_RUNNING);
        this.ObservableAlarmTimerState.postValue(ALARMTIMER_STATE.NOT_RUNNING);
        this.AlarmTimerState = ALARMTIMER_STATE.NOT_RUNNING;

        this.ID = _id;
        this.Title = _title;
        this.LengthInSeconds = _lengthInSeconds;
        this.SaveType = _saveType;
        ConvertProgressToReadableTimer();
    }

    public void Update(String _title, int _lengthInSeconds, ALARMTIMER_SAVE_TYPE _saveType) {
        Pause();
        Reset();

        this.Title = _title;
        this.LengthInSeconds = _lengthInSeconds;
        this.SaveType = _saveType;
        ConvertProgressToReadableTimer();
    }

    public void StartWithOffset(AlarmTimerOffset offset) {
        Log.d("DebugService", "AlarmTimer: SetOffset 1");
        if (AlarmTimerState == ALARMTIMER_STATE.RUNNING || AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            return;
        }

        Log.d("DebugService", "AlarmTimer: SetOffset 2");
        WhenTimerStartedInSeconds = offset.SecondsStartOffset;
        ResumeSecondsOffset = offset.SecondsPauseOffset;
        if (!CalculateIfTimerComplete()) {
            Log.d("DebugService", "AlarmTimer: SetOffset 3");
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
        Log.d("DebugService", "AlarmTimer: StartTimer: 1");
        if (!CalculateIfTimerComplete()) {
            Log.d("DebugService", "AlarmTimer: StartTimer: 2");
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

    public void SetTimerCompleted() {
        // Prevent double event firing
        if (AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            return;
        }

        ObservableAlarmTimerState.postValue(ALARMTIMER_STATE.COMPLETED);
        AlarmTimerState = ALARMTIMER_STATE.COMPLETED;
        NotifyAlarmTimerCompleted(ID);
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
    }

    private boolean CalculateIfTimerComplete() {
        long timerProgress = CalculateTimerProgress();
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
        long numberToFormat;

        if (AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            numberToFormat = 0;
        } else if (AlarmTimerState == ALARMTIMER_STATE.NOT_RUNNING) {
            numberToFormat = LengthInSeconds;
        } else {
            numberToFormat = CalculateRemainingSeconds();
        }

        /*
            numberToFormat might be lower than 0, force to 0
            Issue : "AlarmManager.setExact(AlarmManager.RTC_WAKEUP" is not 100% exact.
            Info  : If long timer is started and app was in idle mode, when timer is complete it might be some seconds later than it should and when
            app is started again timers readable timer is being set with "SetTimerOffsets / SetTimerOffset" so the remaining time might show below  0.
        */
        if (numberToFormat < 0) {
            numberToFormat = 0;
        }

        String readableTimer = DateUtils.formatElapsedTime(numberToFormat);
        this.ReadableTimer.postValue(readableTimer);
    }

    //#region Events
    @Override
    public void RegisterObserver(IAlarmTimerObserver observer) {
        if (!AlarmTimerObservers.contains(observer)) {
            AlarmTimerObservers.add(observer);
        }
    }

    @Override
    public void RemoveObserver(IAlarmTimerObserver observer) {
        if (AlarmTimerObservers.contains(observer)) {
            AlarmTimerObservers.remove(observer);
        }
    }

    @Override
    public void NotifyAlarmTimerStarted(int alarmTimerID) {
        for (IAlarmTimerObserver observer : AlarmTimerObservers) {
            observer.OnAlarmTimerStarted(alarmTimerID);
        }
    }

    @Override
    public void NotifyAlarmTimerResumed(int alarmTimerID) {
        for (IAlarmTimerObserver observer : AlarmTimerObservers) {
            observer.OnAlarmTimerResumed(alarmTimerID);
        }
    }

    @Override
    public void NotifyAlarmTimerPaused(int alarmTimerID) {
        for (IAlarmTimerObserver observer : AlarmTimerObservers) {
            observer.OnAlarmTimerPaused(alarmTimerID);
        }
    }

    @Override
    public void NotifyAlarmTimerReset(int alarmTimerID) {
        for (IAlarmTimerObserver observer : AlarmTimerObservers) {
            observer.OnAlarmTimerReset(alarmTimerID);
        }
    }

    @Override
    public void NotifyAlarmTimerCompleted(int alarmTimerID) {
        for (IAlarmTimerObserver observer : AlarmTimerObservers) {
            observer.OnAlarmTimerCompleted(alarmTimerID);
        }
    }
    //#endregion
}
