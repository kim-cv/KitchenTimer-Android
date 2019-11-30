package com.funkyqubits.kitchentimer.Interfaces;

import java.util.UUID;

public interface IAlarmTimerClickedSubject {
    void RegisterObserver(IAlarmTimerClickObserver observer);

    void RemoveObserver(IAlarmTimerClickObserver observer);

    void NotifyAlarmTimerStart(UUID alarmTimerID);

    void NotifyAlarmTimerPause(UUID alarmTimerID);

    void NotifyAlarmTimerReset(UUID alarmTimerID);
}