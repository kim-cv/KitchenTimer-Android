package com.funkyqubits.kitchentimer.Repositories;

import com.funkyqubits.kitchentimer.Models.AlarmTimer;

import java.util.List;

public interface IRepository {
    List<AlarmTimer> LoadAlarmTimers();
    void SaveAlarmTimers(List<AlarmTimer> alarmTimers);
}
