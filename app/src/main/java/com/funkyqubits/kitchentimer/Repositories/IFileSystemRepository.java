package com.funkyqubits.kitchentimer.Repositories;

import com.funkyqubits.kitchentimer.models.AlarmTimer;

import java.util.ArrayList;

public interface IFileSystemRepository {
    ArrayList<AlarmTimer> LoadAlarmTimers();
    void SaveAlarmTimers(ArrayList<AlarmTimer> alarmTimers);
}
