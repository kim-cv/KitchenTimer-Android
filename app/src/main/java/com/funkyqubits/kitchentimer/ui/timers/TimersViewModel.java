package com.funkyqubits.kitchentimer.ui.timers;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.funkyqubits.kitchentimer.Repositories.IRepository;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class TimersViewModel extends ViewModel {

    public MutableLiveData<ArrayList<AlarmTimer>> ObservableAlarmTimers = new MutableLiveData<>();
    private ArrayList<AlarmTimer> AlarmTimers = new ArrayList<>();
    private IRepository TimerRepository;

    public TimersViewModel() {
        InitTimer();
    }

    // TODO: Figure out how to use dependency injection in Android MVVM
    public void ProvideRepository(IRepository _timerRepository) {
        this.TimerRepository = _timerRepository;
        AlarmTimers.addAll(TimerRepository.LoadAlarmTimers());
        ObservableAlarmTimers.setValue(AlarmTimers);
    }

    public void StartTimer(UUID id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.Start();
    }

    public void PauseTimer(UUID id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.Pause();
    }

    public void ResetTimer(UUID id) {
        AlarmTimer alarmTimer = FindTimerOnId(id);
        if (alarmTimer == null) {
            return;
        }

        alarmTimer.Reset();
    }

    private AlarmTimer FindTimerOnId(UUID id) {
        for (AlarmTimer alarmTimer : AlarmTimers) {
            if (alarmTimer.ID == id) {
                return alarmTimer;
            }
        }
        return null;
    }

    private void InitTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                for (int i = 0; i < AlarmTimers.size(); i++) {
                    AlarmTimer alarmTimer = AlarmTimers.get(i);
                    if (alarmTimer.AlarmTimerState == AlarmTimer.ALARMTIMER_STATE.RUNNING) {
                        alarmTimer.Tick();
                    }
                }
            }
        }, 0, 1000);//Update every second
    }
}