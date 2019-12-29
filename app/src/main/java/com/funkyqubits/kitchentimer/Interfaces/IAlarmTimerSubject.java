package com.funkyqubits.kitchentimer.Interfaces;

public interface IAlarmTimerSubject {
    void RegisterObserver(IAlarmTimerObserver observer);

    void RemoveObserver(IAlarmTimerObserver observer);

    void NotifyAlarmTimerStarted(int alarmTimerID);

    void NotifyAlarmTimerResumed(int alarmTimerID);

    void NotifyAlarmTimerPaused(int alarmTimerID);

    void NotifyAlarmTimerReset(int alarmTimerID);

    void NotifyAlarmTimerCompleted(int alarmTimerID);
}
