package com.funkyqubits.kitchentimer.Interfaces;

public interface IAlarmTimerClickedSubject {
    void RegisterObserver(IAlarmTimerClickObserver observer);

    void RemoveObserver(IAlarmTimerClickObserver observer);

    void NotifyAlarmTimerStart(int alarmTimerID);

    void NotifyAlarmTimerPause(int alarmTimerID);

    void NotifyAlarmTimerReset(int alarmTimerID);

    void NotifyAlarmTimerDelete(int alarmTimerID);
}