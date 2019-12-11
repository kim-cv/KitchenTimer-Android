package com.funkyqubits.kitchentimer.Controller;

import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.funkyqubits.kitchentimer.Repositories.IRepository;

import java.util.ArrayList;
import java.util.Map;

public class TimerController {

    public ArrayList<AlarmTimer> AlarmTimers = new ArrayList<>();
    private IRepository TimerRepository;

    public TimerController(IRepository _timerRepository) {
        this.TimerRepository = _timerRepository;

        /*
            Old app didn't use unique ID's so for backward compatibility loading old timers from storage receive -1 as id
            When timers are loaded this piece of code check for timers with -1 as id and assign a new unique id
        */
        ArrayList<AlarmTimer> tmpAlarmTimers = TimerRepository.LoadAlarmTimers();
        for (AlarmTimer tmpAlarmTimer : tmpAlarmTimers) {
            if (tmpAlarmTimer.ID == -1) {
                int newRandomId = GenerateUniqueIntId();
                tmpAlarmTimer.ID = newRandomId;
            }
        }

        AlarmTimers.addAll(tmpAlarmTimers);
    }

    public void SaveAllTimersToStorage() {
        TimerRepository.SaveAlarmTimers(GetTimersThatShouldBeSaved());
    }

    public void SetInitialTimerValues(Map<String, Long> alarmTimers) {
        for (Map.Entry<String, Long> alarmTimerEntry : alarmTimers.entrySet()) {
            int id = Integer.parseInt(alarmTimerEntry.getKey());
            Long whenTimerBegun = alarmTimerEntry.getValue();

            StartTimer(id, whenTimerBegun);
        }
    }

    public ArrayList<AlarmTimer> GetRunningTimers() {
        ArrayList<AlarmTimer> tmpAlarmTimers = new ArrayList<>();
        for (AlarmTimer alarmTimer : AlarmTimers) {
            if (alarmTimer.AlarmTimerState.getValue() == AlarmTimer.ALARMTIMER_STATE.RUNNING) {
                tmpAlarmTimers.add(alarmTimer);
            }
        }
        return tmpAlarmTimers;
    }
    public ArrayList<AlarmTimer> GetTimersThatShouldBeSaved() {
        ArrayList<AlarmTimer> tmpAlarmTimers = new ArrayList<>();
        for (AlarmTimer alarmTimer : AlarmTimers) {
            if (alarmTimer.SaveType == AlarmTimer.ALARMTIMER_SAVE_TYPE.SAVE) {
                tmpAlarmTimers.add(alarmTimer);
            }
        }
        return tmpAlarmTimers;
    }

    public AlarmTimer CreateTimer(String title, int lengthInSeconds, AlarmTimer.ALARMTIMER_SAVE_TYPE saveType) {
        int newId = GenerateUniqueIntId();
        AlarmTimer alarmTimer = new AlarmTimer(newId, title, lengthInSeconds, saveType);
        AlarmTimers.add(alarmTimer);
        return alarmTimer;
    }

    public void StartTimer(int id, long whenTimerBegun) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.Start(whenTimerBegun);
    }

    public void StartTimer(int id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.Start();
    }

    public void PauseTimer(int id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.Pause();
    }

    public void ResetTimer(int id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.Reset();
    }

    public AlarmTimer FindTimerOnId(int id) {
        for (AlarmTimer alarmTimer : AlarmTimers) {
            if (alarmTimer.ID == id) {
                return alarmTimer;
            }
        }
        return null;
    }

    private int GenerateUniqueIntId() {
        int randomId = (int) (Math.random() * ((99999999 - 1) + 1)) + 1;
        AlarmTimer tmpAlarmTimer = FindTimerOnId(randomId);

        if (tmpAlarmTimer == null) {
            return randomId;
        } else {
            return GenerateUniqueIntId();
        }
    }
}
