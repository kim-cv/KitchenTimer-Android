package com.funkyqubits.kitchentimer.models;

import android.text.format.DateUtils;

import androidx.lifecycle.MutableLiveData;

import java.util.Calendar;
import java.util.UUID;

public class AlarmTimer {

    public ALARMTIMER_STATE AlarmTimerState = ALARMTIMER_STATE.NOT_RUNNING;
    public UUID ID;
    public String Title;
    public MutableLiveData<String> ReadableTimer = new MutableLiveData<>();
    public int LengthInSeconds;

    public long WhenTimerStartedInSeconds;

    public enum ALARMTIMER_STATE {
        RUNNING,
        NOT_RUNNING,
        PAUSED,
        COMPLETED
    }

    public AlarmTimer(UUID _id, String _title, int _lengthInSeconds) {
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
        if (AlarmTimerState == ALARMTIMER_STATE.RUNNING || AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            return;
        }

        WhenTimerStartedInSeconds = Calendar.getInstance().getTimeInMillis() / 1000;
        StartTimer();
    }

    private void StartTimer() {
        AlarmTimerState = ALARMTIMER_STATE.RUNNING;
        CalculateIfTimerComplete();
        ConvertProgressToReadableTimer();
    }


    public void Pause() {
        if (AlarmTimerState == ALARMTIMER_STATE.PAUSED || AlarmTimerState == ALARMTIMER_STATE.NOT_RUNNING || AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            return;
        }

        AlarmTimerState = ALARMTIMER_STATE.PAUSED;
        ConvertProgressToReadableTimer();
    }

    public void Reset() {
        if (AlarmTimerState == ALARMTIMER_STATE.RUNNING || AlarmTimerState == ALARMTIMER_STATE.NOT_RUNNING) {
            return;
        }

        AlarmTimerState = ALARMTIMER_STATE.NOT_RUNNING;
        WhenTimerStartedInSeconds = 0;
        ConvertProgressToReadableTimer();
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
        }
    }

    private Long CalculateTimerProgress() {
        long nowSeconds = Calendar.getInstance().getTimeInMillis() / 1000;
        long timerProgress = nowSeconds - WhenTimerStartedInSeconds;
        return  timerProgress;
    }

    private void ConvertProgressToReadableTimer() {
        long progress = CalculateTimerProgress();
        if (AlarmTimerState == ALARMTIMER_STATE.COMPLETED || AlarmTimerState == ALARMTIMER_STATE.NOT_RUNNING) {
            this.ReadableTimer.postValue(DateUtils.formatElapsedTime(LengthInSeconds));
        } else {
            this.ReadableTimer.postValue(DateUtils.formatElapsedTime(LengthInSeconds - progress));
        }
    }
}
