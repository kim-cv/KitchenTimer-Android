package com.funkyqubits.kitchentimer.Repositories;

import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.funkyqubits.kitchentimer.models.AlarmTimerOffset;

import java.util.ArrayList;
import java.util.Map;

public interface ISharedPreferencesRepository {
    ArrayList<AlarmTimerOffset> GetOffsets();
    Map<String, Long> LoadRunningTimersStartOffset();
    void SaveRunningTimersStartOffset(ArrayList<AlarmTimer> alarmTimers);
    Map<String, Long> LoadRunningTimersPauseOffsets();
    void SaveRunningTimersPauseOffsets(ArrayList<AlarmTimer> alarmTimers);
}
