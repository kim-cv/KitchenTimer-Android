package com.funkyqubits.kitchentimer.ui.timers;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.funkyqubits.kitchentimer.Controller.TimerController;
import com.funkyqubits.kitchentimer.Models.AlarmTimer;
import com.funkyqubits.kitchentimer.Repositories.IRepository;

import java.util.ArrayList;
import java.util.UUID;

public class TimersViewModel extends ViewModel {

    private TimerController TimerController;
    public MutableLiveData<ArrayList<AlarmTimer>> alarmTimers = new MutableLiveData<>();

    public TimersViewModel() {
    }

    public void ProvideRepository(IRepository _timerRepository){
        // TODO: Figure out how to use dependency injection in Android MVVM
        TimerController = new TimerController(_timerRepository);
        alarmTimers.setValue(TimerController.GetTimers());
    }

    public void StartTimer(UUID id) {
        TimerController.StartTimer(id);
    }

    public void PauseTimer(UUID id) {
        TimerController.PauseTimer(id);
    }

    public void ResetTimer(UUID id) {
        TimerController.ResetTimer(id);
    }
}