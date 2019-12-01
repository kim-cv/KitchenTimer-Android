package com.funkyqubits.kitchentimer.Interfaces;

import java.util.UUID;

public interface IAlarmTimerCompleteObserver {
    void OnAlarmTimerStarted(UUID alarmTimerID);

    void OnAlarmTimerResumed(UUID alarmTimerID);

    void OnAlarmTimerPaused(UUID alarmTimerID);

    void OnAlarmTimerReset(UUID alarmTimerID);

    void OnAlarmTimerCompleted(UUID alarmTimerID);
}
