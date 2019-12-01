package com.funkyqubits.kitchentimer.Interfaces;

import java.util.UUID;

public interface IAlarmTimerCompleteSubject {
    void RegisterObserver(IAlarmTimerCompleteObserver observer);

    void RemoveObserver(IAlarmTimerCompleteObserver observer);

    void NotifyAlarmTimerComplete(UUID alarmTimerID);
}
