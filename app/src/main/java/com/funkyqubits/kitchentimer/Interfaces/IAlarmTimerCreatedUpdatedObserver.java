package com.funkyqubits.kitchentimer.Interfaces;

public interface IAlarmTimerCreatedUpdatedObserver {
    void OnAlarmTimerCreated(int alarmTimerID);

    void OnAlarmTimerUpdated(int alarmTimerID);
}
