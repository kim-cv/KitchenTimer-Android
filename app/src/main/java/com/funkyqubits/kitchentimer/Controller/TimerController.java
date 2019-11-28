package com.funkyqubits.kitchentimer.Controller;

import com.funkyqubits.kitchentimer.Models.AlarmTimer;
import com.funkyqubits.kitchentimer.Repositories.IRepository;

import java.util.ArrayList;
import java.util.UUID;

public class TimerController {

    private ArrayList<AlarmTimer> AlarmTimers;
    private IRepository TimerRepository;

    public TimerController(IRepository _timerRepository) {
        this.TimerRepository = _timerRepository;
        AlarmTimers = TimerRepository.LoadAlarmTimers();
    }

    public void StartTimer(UUID id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        //alarmTimer.Start();
    }

    public void PauseTimer(UUID id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        //alarmTimer.Pause();
    }

    public void ResetTimer(UUID id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        //alarmTimer.Reset();
    }

    private AlarmTimer FindTimerOnId(UUID id) {
        for(AlarmTimer alarmTimer : AlarmTimers) {
            if(alarmTimer.ID == id) {
                return alarmTimer;
            }
        }
        return null;
    }
}
