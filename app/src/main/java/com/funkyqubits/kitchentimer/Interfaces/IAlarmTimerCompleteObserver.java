package com.funkyqubits.kitchentimer.Interfaces;

import java.util.UUID;

public interface IAlarmTimerCompleteObserver {
    void OnComplete(UUID alarmTimerID);
}
