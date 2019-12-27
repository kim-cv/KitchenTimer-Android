package com.funkyqubits.kitchentimer.Interfaces;

public interface IAlarmTimerCreatedUpdatedObserver {
    void OnCreated(int alarmTimerID);

    void OnUpdated(int alarmTimerID);
}
