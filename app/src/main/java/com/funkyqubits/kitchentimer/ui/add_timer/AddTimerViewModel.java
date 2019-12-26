package com.funkyqubits.kitchentimer.ui.add_timer;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.funkyqubits.kitchentimer.Controller.TimerController;

import java.util.Dictionary;
import java.util.Hashtable;

public class AddTimerViewModel extends ViewModel {

    private TimerController TimerController;

    public MutableLiveData<String> Title = new MutableLiveData<>();
    public MutableLiveData<Integer> NumberPicker_hours = new MutableLiveData<>();
    public MutableLiveData<Integer> NumberPicker_minutes = new MutableLiveData<>();
    public MutableLiveData<Integer> NumberPicker_seconds = new MutableLiveData<>();
    public MutableLiveData<Integer> RadioGroup_saveType = new MutableLiveData<>();

    public MutableLiveData<String> title_error = new MutableLiveData<>();
    public MutableLiveData<String> timer_length_error = new MutableLiveData<>();
    public MutableLiveData<String> saveOrSingle_error = new MutableLiveData<>();
    public MutableLiveData<Boolean> btn_clickable = new MutableLiveData<>();

    public AddTimerViewModel() {
    }

    // TODO: Figure out how to use dependency injection in Android MVVM
    public void ProvideExtra(TimerController _timerController) {
        this.TimerController = _timerController;
        Title.setValue("");
        NumberPicker_hours.setValue(0);
        NumberPicker_minutes.setValue(0);
        NumberPicker_seconds.setValue(0);
        RadioGroup_saveType.setValue(R.id.radioButton_saveOrSingle_save);
        btn_clickable.setValue(false);
    }

    public void SaveAllTimersToStorage() {
        TimerController.SaveAllTimersToStorage();
    }

    /*
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
    */

    public void CreateTimer() {

    }

    public boolean ValidateViewData(boolean toggleErrors) {
        int numErrors = 0;

        boolean resultTitle = IsTitleValid();
        if (!resultTitle) {
            numErrors += 1;
        }

        Dictionary<String, Boolean> resultNumberpickers = IsNumberpickersValid();
        if (!resultNumberpickers.get("no_value") || !resultNumberpickers.get("hours") || !resultNumberpickers.get("minutes") || !resultNumberpickers.get("seconds")) {
            numErrors += 1;
        }

        boolean resultRadiogroup = IsRadiogroupValid();
        if (!resultRadiogroup) {
            numErrors += 1;
        }

        if (toggleErrors) {
            ToggleTitleError(resultTitle);
            ToggleNumberpickerErrors(resultNumberpickers);
            ToggleRadiogroupError(resultRadiogroup);
        }

        return numErrors <= 0;
    }

    public void ToggleButtonEnabled(boolean enable) {
        this.btn_clickable.setValue(enable);
    }

    public boolean IsTitleValid() {
        String value = Title.getValue();
        if (value.length() <= 0) {
            return false;
        } else {
            return true;
        }
    }

    public void ToggleTitleError(boolean isValid) {
        if (!isValid) {
            title_error.setValue("Must provide title.");
        } else {
            if (title_error.getValue() != null) {
                title_error.setValue(null);
            }
        }
    }

    public Dictionary<String, Boolean> IsNumberpickersValid() {
        Hashtable errors = new Hashtable();

        int hours = NumberPicker_hours.getValue();
        int minutes = NumberPicker_minutes.getValue();
        int seconds = NumberPicker_seconds.getValue();

        boolean hours_range = (hours < 0 || hours > 23);
        boolean minutes_range = (minutes < 0 || minutes > 59);
        boolean seconds_range = (seconds < 0 || seconds > 59);
        boolean no_value = (hours <= 0 && minutes <= 0 && seconds <= 0);

        errors.put("no_value", !no_value);
        errors.put("hours", !hours_range);
        errors.put("minutes", !minutes_range);
        errors.put("seconds", !seconds_range);

        return errors;
    }

    public void ToggleNumberpickerErrors(Dictionary<String, Boolean> errors) {
        if (!errors.get("no_value")) {
            timer_length_error.setValue("Must choose timer length.");
        } else if (!errors.get("hours")) {
            timer_length_error.setValue("Hours must be between 0 and 23.");
        } else if (!errors.get("minutes")) {
            timer_length_error.setValue("Minutes must be between 0 and 59.");
        } else if (!errors.get("seconds")) {
            timer_length_error.setValue("Seconds must be between 0 and 59.");
        } else {
            // Remove err
            if (timer_length_error.getValue() != null) {
                timer_length_error.setValue(null);
            }
        }
    }

    public boolean IsRadiogroupValid() {
        int selected_radioButton_id = RadioGroup_saveType.getValue();
        switch (selected_radioButton_id) {
            case -1: {
                // Nothing selected
                return false;
            }
            default: {
                return true;
            }
        }
    }

    public void ToggleRadiogroupError(boolean isValid) {
        if (!isValid) {
            saveOrSingle_error.setValue("Must choose timer type.");
        } else {
            // Remove err
            if (saveOrSingle_error.getValue() != null) {
                saveOrSingle_error.setValue(null);
            }
        }
    }
}
