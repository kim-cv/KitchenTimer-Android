package com.funkyqubits.kitchentimer.Interfaces;

public interface IAlarmTimerCompleteObserver {
    void OnAlarmTimerStarted(int alarmTimerID);

    void OnAlarmTimerResumed(int alarmTimerID);

    void OnAlarmTimerPaused(int alarmTimerID);

    void OnAlarmTimerReset(int alarmTimerID);

    void OnAlarmTimerCompleted(int alarmTimerID);
}
