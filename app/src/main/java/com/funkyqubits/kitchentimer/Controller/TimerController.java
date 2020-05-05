package com.funkyqubits.kitchentimer.Controller;

import com.funkyqubits.kitchentimer.Repositories.ISharedPreferencesRepository;
import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.funkyqubits.kitchentimer.Repositories.IFileSystemRepository;
import com.funkyqubits.kitchentimer.models.AlarmTimerOffset;

import java.util.ArrayList;

public final class TimerController {

    private static TimerController _instance;

    public static TimerController Instance(IFileSystemRepository _timerRepository, ISharedPreferencesRepository _sharedPreferencesTimerOffsetsRepository) {
        if (_instance == null) {
            _instance = new TimerController(_timerRepository, _sharedPreferencesTimerOffsetsRepository);
        }
        return _instance;
    }

    public ArrayList<AlarmTimer> AlarmTimers = new ArrayList<>();
    private IFileSystemRepository TimerRepository;
    private ISharedPreferencesRepository RunningTimersDataRepository;

    private TimerController(IFileSystemRepository _timerRepository, ISharedPreferencesRepository _sharedPreferencesTimerOffsetsRepository) {
        this.TimerRepository = _timerRepository;
        this.RunningTimersDataRepository = _sharedPreferencesTimerOffsetsRepository;

        /*
            Old app didn't use unique ID's so for backward compatibility loading old timers from storage receive -1 as id
            When timers are loaded this piece of code check for timers with -1 as id and assign a new unique id
        */
        ArrayList<AlarmTimer> tmpAlarmTimers = TimerRepository.LoadAlarmTimers();
        for (AlarmTimer tmpAlarmTimer : tmpAlarmTimers) {
            if (tmpAlarmTimer.ID == -1) {
                tmpAlarmTimer.ID = GenerateUniqueIntId();
            }
        }

        AlarmTimers.addAll(tmpAlarmTimers);

        SetTimerOffsets();
    }

    public void SaveAllTimersToStorage() {
        ArrayList<AlarmTimer> runningAlarmTimers = GetRunningTimers();
        RunningTimersDataRepository.SaveRunningTimersStartOffset(runningAlarmTimers);
        RunningTimersDataRepository.SaveRunningTimersPauseOffsets(runningAlarmTimers);

        TimerRepository.SaveAlarmTimers(GetTimersThatShouldBeSaved());
    }

    private ArrayList<AlarmTimer> GetRunningTimers() {
        ArrayList<AlarmTimer> tmpAlarmTimers = new ArrayList<>();
        for (AlarmTimer alarmTimer : AlarmTimers) {
            if (alarmTimer.AlarmTimerState == AlarmTimer.ALARMTIMER_STATE.RUNNING) {
                tmpAlarmTimers.add(alarmTimer);
            }
        }
        return tmpAlarmTimers;
    }

    private ArrayList<AlarmTimer> GetTimersThatShouldBeSaved() {
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

    public void UpdateTimer(int alarmTimerId, String title, int lengthInSeconds, AlarmTimer.ALARMTIMER_SAVE_TYPE saveType) {
        AlarmTimer alarmTimer = FindTimerOnId(alarmTimerId);
        if (alarmTimer == null) {
            return;
        }
        alarmTimer.Update(title, lengthInSeconds, saveType);
    }

    //#region Timer offsets
    private void SetTimerOffsets() {
        ArrayList<AlarmTimerOffset> timerOffsets = this.RunningTimersDataRepository.GetOffsets();
        for (AlarmTimerOffset offset : timerOffsets) {
            SetTimerOffset(offset);
        }
    }

    private void SetTimerOffset(AlarmTimerOffset offset) {
        AlarmTimer alarmTimer = FindTimerOnId(offset.ID);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.SetOffset(offset);
    }
    //#endregion Timer offsets

    public void StartTimer(int id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        if (alarmTimer.AlarmTimerState == AlarmTimer.ALARMTIMER_STATE.NOT_RUNNING) {
            alarmTimer.Start();
        } else {
            alarmTimer.Resume();
        }
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

    public void DeleteTimer(int id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        AlarmTimers.remove(alarmTimer);
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
