package com.funkyqubits.kitchentimer.Models;

import android.text.format.DateUtils;

public class AlarmTimer {
    public int ID = 0;
    public String ReadableTimer = "";
    public boolean IsTimerComplete = false;
    private int LengthInSeconds = 0;
    private int ProgressInSeconds = 0;

    public AlarmTimer(int _id, int _lengthInSeconds) {
        this.ID = _id;
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
