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

    private long WhenTimerStartedInSeconds;
    private long SecondsPassedAtTimeOfTick = 0;

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

    public void Start() {
        if (AlarmTimerState == ALARMTIMER_STATE.RUNNING || AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            return;
        }

        WhenTimerStartedInSeconds = Calendar.getInstance().getTimeInMillis() / 1000;
        AlarmTimerState = ALARMTIMER_STATE.RUNNING;
    }

    public void Pause() {
        if (AlarmTimerState == ALARMTIMER_STATE.PAUSED || AlarmTimerState == ALARMTIMER_STATE.NOT_RUNNING || AlarmTimerState == ALARMTIMER_STATE.COMPLETED) {
            return;
        }

        AlarmTimerState = ALARMTIMER_STATE.PAUSED;
    }

    public void Reset() {
        if (AlarmTimerState == ALARMTIMER_STATE.RUNNING || AlarmTimerState == ALARMTIMER_STATE.NOT_RUNNING) {
            return;
        }

        AlarmTimerState = ALARMTIMER_STATE.NOT_RUNNING;
        WhenTimerStartedInSeconds = -1;
        ConvertProgressToReadableTimer();
    }

    /**
     * Is called each second
     */
    public void Tick() {
        SecondsPassedAtTimeOfTick = Calendar.getInstance().getTimeInMillis() / 1000;
        if (AlarmTimerState != ALARMTIMER_STATE.RUNNING) {
            return;
        }

        ConvertProgressToReadableTimer();

        if ((SecondsPassedAtTimeOfTick - WhenTimerStartedInSeconds) >= LengthInSeconds) {
            AlarmTimerState = ALARMTIMER_STATE.COMPLETED;
        }
    }

    private void ConvertProgressToReadableTimer() {
        long progress = 0;
        if (WhenTimerStartedInSeconds > 0) {
            progress = SecondsPassedAtTimeOfTick - WhenTimerStartedInSeconds;
        }

        this.ReadableTimer.postValue(DateUtils.formatElapsedTime(LengthInSeconds - progress));
    }
}
