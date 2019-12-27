package com.funkyqubits.kitchentimer.Interfaces;

public interface IAlarmTimerCreatedUpdatedSubject {
    void RegisterObserver(IAlarmTimerCreatedUpdatedObserver observer);

    void RemoveObserver(IAlarmTimerCreatedUpdatedObserver observer);

    void NotifyAlarmTimerCreated(int alarmTimerID);

    void NotifyAlarmTimerUpdated(int alarmTimerID);
}