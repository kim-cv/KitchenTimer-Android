package com.funkyqubits.kitchentimer.Interfaces;

public interface IAlarmTimerCompleteSubject {
    void RegisterObserver(IAlarmTimerCompleteObserver observer);

    void RemoveObserver(IAlarmTimerCompleteObserver observer);

    void NotifyAlarmTimerStarted(int alarmTimerID);

    void NotifyAlarmTimerResumed(int alarmTimerID);

    void NotifyAlarmTimerPaused(int alarmTimerID);

    void NotifyAlarmTimerReset(int alarmTimerID);

    void NotifyAlarmTimerCompleted(int alarmTimerID);
}
