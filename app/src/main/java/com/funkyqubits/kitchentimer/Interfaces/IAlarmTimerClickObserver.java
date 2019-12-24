package com.funkyqubits.kitchentimer.Interfaces;

public interface IAlarmTimerClickObserver {
    void OnStart(int alarmTimerID);

    void OnPause(int alarmTimerID);

    void OnReset(int alarmTimerID);

    void OnEdit(int alarmTimerID);

    void OnDelete(int alarmTimerID);
}
