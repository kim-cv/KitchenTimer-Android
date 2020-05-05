package com.funkyqubits.kitchentimer.Interfaces;

public interface IAlarmTimerUIEventsSubject {
    void RegisterObserver(IAlarmTimerUIEventsObserver observer);

    void RemoveObserver(IAlarmTimerUIEventsObserver observer);

    void NotifyOfUIAlarmTimerStart(int alarmTimerID);

    void NotifyOfUIAlarmTimerPause(int alarmTimerID);

    void NotifyOfUIAlarmTimerReset(int alarmTimerID);

    void NotifyOfUIAlarmTimerEdit(int alarmTimerID);

    void NotifyOfUIAlarmTimerDelete(int alarmTimerID);
}