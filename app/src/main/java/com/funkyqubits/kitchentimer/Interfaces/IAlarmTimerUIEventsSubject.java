package com.funkyqubits.kitchentimer.Interfaces;

public interface IAlarmTimerUIEventsSubject {
    void RegisterObserver(IAlarmTimerUIEventsObserver observer);

    void RemoveObserver(IAlarmTimerUIEventsObserver observer);

    void NotifyAlarmTimerStart(int alarmTimerID);

    void NotifyAlarmTimerPause(int alarmTimerID);

    void NotifyAlarmTimerReset(int alarmTimerID);

    void NotifyAlarmTimerEdit(int alarmTimerID);

    void NotifyAlarmTimerDelete(int alarmTimerID);
}