package com.funkyqubits.kitchentimer.Interfaces;

public interface IAlarmTimerUIEventsObserver {
    void OnUIAlarmTimerStart(int alarmTimerID);

    void OnUIAlarmTimerPause(int alarmTimerID);

    void OnUIAlarmTimerReset(int alarmTimerID);

    void OnUIAlarmTimerEdit(int alarmTimerID);

    void OnUIAlarmTimerDelete(int alarmTimerID);
}
