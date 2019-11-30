package com.funkyqubits.kitchentimer.Interfaces;

import java.util.UUID;

public interface IAlarmTimerClickObserver {
    void OnStart(UUID alarmTimerID);

    void OnPause(UUID alarmTimerID);

    void OnReset(UUID alarmTimerID);
}
