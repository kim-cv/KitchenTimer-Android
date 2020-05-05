package com.funkyqubits.kitchentimer.Repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.funkyqubits.kitchentimer.models.AlarmTimerOffset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SharedPreferencesRepository implements ISharedPreferencesRepository {
    private Context Context;
    private String Filename_RunningTimers;
    private String Filename_RunningTimersOffsets;

    public SharedPreferencesRepository(Context _context) {
        Context = _context;
        Filename_RunningTimers = Context.getString(R.string.preference_file_runningTimers);
        Filename_RunningTimersOffsets = Context.getString(R.string.preference_file_runningTimers_offsets);
    }

    public ArrayList<AlarmTimerOffset> GetOffsets() {
        ArrayList<AlarmTimerOffset> timerOffsets = new ArrayList<>();

        Map<String, Long> startOffsets = LoadRunningTimersStartOffset();
        Map<String, Long> pauseOffsets = LoadRunningTimersPauseOffsets();

        for (Map.Entry<String, Long> alarmTimerEntry : startOffsets.entrySet()) {
            int id = Integer.parseInt(alarmTimerEntry.getKey());

            AlarmTimerOffset tmpTimerOffset = new AlarmTimerOffset();
            tmpTimerOffset.ID = id;
            tmpTimerOffset.SecondsStartOffset = alarmTimerEntry.getValue();
            timerOffsets.add(tmpTimerOffset);
        }

        for(AlarmTimerOffset alarmTimerOffset : timerOffsets) {
            String timerIdAsString = Integer.toString(alarmTimerOffset.ID);
            if (!pauseOffsets.containsKey(timerIdAsString)) {
                continue;
            }

            Long pauseOffset = pauseOffsets.get(timerIdAsString);
            alarmTimerOffset.SecondsPauseOffset = pauseOffset;
        }

        return timerOffsets;
    }

    @Override
    public Map<String, Long> LoadRunningTimersStartOffset() {
        Map<String, Long> runningTimersData = new HashMap<>();

        // Get SharedPreferences
        SharedPreferences sharedPreferences_runningTimers = GetSharedPreferences(Filename_RunningTimers);

        // Map sharedPreference data
        Map<String, ?> allEntries = sharedPreferences_runningTimers.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Long parseLong = Long.parseLong(entry.getValue().toString());
            runningTimersData.put(entry.getKey(), parseLong);
        }

        return runningTimersData;
    }

    @Override
    public Map<String, Long> LoadRunningTimersPauseOffsets() {
        Map<String, Long> runningTimersData = new HashMap<>();

        // Get SharedPreferences
        SharedPreferences sharedPreferences_runningTimers = GetSharedPreferences(Filename_RunningTimersOffsets);

        // Map sharedPreference data
        Map<String, ?> allEntries = sharedPreferences_runningTimers.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Long parseLong = Long.parseLong(entry.getValue().toString());
            runningTimersData.put(entry.getKey(), parseLong);
        }

        return runningTimersData;
    }

    @Override
    public void SaveRunningTimersStartOffset(ArrayList<AlarmTimer> runningAlarmTimers) {
        // Get SharedPreferences Editor
        SharedPreferences.Editor sharedPreferences_editor_runningTimers = GetSharedPreferencesEditor(Filename_RunningTimers);

        // Commit timer data
        sharedPreferences_editor_runningTimers.clear();
        for (AlarmTimer alarmTimer : runningAlarmTimers) {
            sharedPreferences_editor_runningTimers.putLong(Integer.toString(alarmTimer.ID), alarmTimer.WhenTimerStartedInSeconds);
        }
        sharedPreferences_editor_runningTimers.commit();
    }

    @Override
    public void SaveRunningTimersPauseOffsets(ArrayList<AlarmTimer> runningAlarmTimers) {
        // Get SharedPreferences Editor
        SharedPreferences.Editor sharedPreferences_editor_runningTimers = GetSharedPreferencesEditor(Filename_RunningTimersOffsets);

        // Commit timer offset data
        sharedPreferences_editor_runningTimers.clear();
        for (AlarmTimer alarmTimer : runningAlarmTimers) {
            sharedPreferences_editor_runningTimers.putLong(Integer.toString(alarmTimer.ID), alarmTimer.ResumeSecondsOffset);
        }
        sharedPreferences_editor_runningTimers.commit();
    }

    private SharedPreferences GetSharedPreferences(String filename) {
        return Context.getSharedPreferences(filename, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor GetSharedPreferencesEditor(String filename) {
        return GetSharedPreferences(filename).edit();
    }
}
