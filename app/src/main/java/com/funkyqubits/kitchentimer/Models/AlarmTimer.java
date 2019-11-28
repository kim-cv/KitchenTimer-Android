package com.funkyqubits.kitchentimer.Models;

public class AlarmTimer {
    public int ID = 0;
    public String ReadableTimer = "";
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
        ProgressInSeconds += 1;
        this.ReadableTimer = ConvertProgressToReadableTimer();

        if (ProgressInSeconds >= LengthInSeconds) {
            // TODO: Implement timer complete logic
        }
    }

    private String ConvertProgressToReadableTimer() throws Exception {
        throw new Exception("Method not implemented yet.");
    }
}
