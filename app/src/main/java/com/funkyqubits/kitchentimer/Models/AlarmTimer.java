package com.funkyqubits.kitchentimer.Models;

import android.text.format.DateUtils;

import java.util.UUID;

public class AlarmTimer {
    public UUID ID;
    public String Title;
    public String ReadableTimer;
    public boolean IsTimerComplete = false;
    public int LengthInSeconds;
    private int ProgressInSeconds;

    public AlarmTimer(UUID _id, String _title, int _lengthInSeconds) {
        this.ID = _id;
        this.Title = _title;
        this.LengthInSeconds = _lengthInSeconds;
    }

    public void Start() throws Exception {
        throw new Exception("Method not implemented yet.");
    }

    public void Pause() throws Exception {
        throw new Exception("Method not implemented yet.");
    }

    public void Reset() throws Exception {
        throw new Exception("Method not implemented yet.");
    }

    /**
     * Is called each second
     */
    public void Tick() {
        if (IsTimerComplete) {
            return;
        }

        ProgressInSeconds += 1;
        this.ReadableTimer = ConvertProgressToReadableTimer();

        if (ProgressInSeconds >= LengthInSeconds) {
            IsTimerComplete = true;
        }
    }

    private String ConvertProgressToReadableTimer() {
        return DateUtils.formatElapsedTime(ProgressInSeconds);
    }
}
