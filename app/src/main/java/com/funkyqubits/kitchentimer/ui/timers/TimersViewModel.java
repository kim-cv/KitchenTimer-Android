package com.funkyqubits.kitchentimer.ui.timers;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.funkyqubits.kitchentimer.Models.AlarmTimer;
import com.funkyqubits.kitchentimer.Repositories.IRepository;

import java.util.ArrayList;
import java.util.UUID;

public class TimersViewModel extends ViewModel {

    public MutableLiveData<ArrayList<AlarmTimer>> ObservableAlarmTimers = new MutableLiveData<>();
    private ArrayList<AlarmTimer> AlarmTimers;
    private IRepository TimerRepository;

    public TimersViewModel() {
    }

    // TODO: Figure out how to use dependency injection in Android MVVM
    public void ProvideRepository(IRepository _timerRepository) {
        this.TimerRepository = _timerRepository;
        AlarmTimers = TimerRepository.LoadAlarmTimers();
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
}