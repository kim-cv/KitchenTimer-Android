package com.funkyqubits.kitchentimer.ui.add_timer;

import androidx.lifecycle.ViewModel;

import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.funkyqubits.kitchentimer.Controller.TimerController;

import java.util.ArrayList;

public class AddTimerViewModel extends ViewModel {

    private TimerController TimerController;

    public AddTimerViewModel() {
    }

    // TODO: Figure out how to use dependency injection in Android MVVM
    public void ProvideExtra(TimerController _timerController) {
        this.TimerController = _timerController;
    }

    public ArrayList<AlarmTimer> GetRunningTimers() {
        return TimerController.GetRunningTimers();
    }

    public void SaveAllTimersToStorage() {
        TimerController.SaveAllTimersToStorage();
    }

    public AlarmTimer CreateTimer(String title, int hours, int minutes, int seconds, boolean saveTimer) {
        int lengthInSeconds = 0;
        lengthInSeconds += ((hours * 60) * 60);
        lengthInSeconds += (minutes * 60);
        lengthInSeconds += seconds;

        AlarmTimer.ALARMTIMER_SAVE_TYPE saveTimerType;
        if (saveTimer == true) {
            saveTimerType = AlarmTimer.ALARMTIMER_SAVE_TYPE.SAVE;
        } else {
            saveTimerType = AlarmTimer.ALARMTIMER_SAVE_TYPE.SINGLE_USE;
        }

        return TimerController.CreateTimer(title, lengthInSeconds, saveTimerType);
    }

}
