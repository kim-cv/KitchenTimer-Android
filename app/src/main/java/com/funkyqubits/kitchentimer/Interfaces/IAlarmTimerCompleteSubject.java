package com.funkyqubits.kitchentimer.Interfaces;

import java.util.UUID;

public interface IAlarmTimerCompleteSubject {
    void RegisterObserver(IAlarmTimerCompleteObserver observer);

    void RemoveObserver(IAlarmTimerCompleteObserver observer);

    void NotifyAlarmTimerStarted(UUID alarmTimerID);

    void NotifyAlarmTimerResumed(UUID alarmTimerID);

    void NotifyAlarmTimerPaused(UUID alarmTimerID);

    void NotifyAlarmTimerReset(UUID alarmTimerID);

    void NotifyAlarmTimerCompleted(UUID alarmTimerID);
}
