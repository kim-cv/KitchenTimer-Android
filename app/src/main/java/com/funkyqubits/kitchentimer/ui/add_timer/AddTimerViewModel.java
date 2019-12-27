package com.funkyqubits.kitchentimer.ui.add_timer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.Controller.TimerController;
import com.funkyqubits.kitchentimer.models.AlarmTimer;

import java.util.Dictionary;
import java.util.Hashtable;

public class AddTimerViewModel extends ViewModel {

    private TimerController TimerController;

    public MutableLiveData<String> Title = new MutableLiveData<>("");
    public MutableLiveData<Integer> NumberPicker_hours = new MutableLiveData<>(0);
    public MutableLiveData<Integer> NumberPicker_minutes = new MutableLiveData<>(0);
    public MutableLiveData<Integer> NumberPicker_seconds = new MutableLiveData<>(0);
    public MutableLiveData<Integer> RadioGroup_saveType = new MutableLiveData<>(R.id.radioButton_saveOrSingle_save);

    public MutableLiveData<String> title_error = new MutableLiveData<>("");
    public MutableLiveData<String> timer_length_error = new MutableLiveData<>("");
    public MutableLiveData<String> saveOrSingle_error = new MutableLiveData<>("");
    public MutableLiveData<Boolean> btn_clickable = new MutableLiveData<>(false);

    public AddTimerViewModel() {
    }

    // TODO: Figure out how to use dependency injection in Android MVVM
    public void ProvideExtra(TimerController _timerController) {
        this.TimerController = _timerController;
    }

    public void SaveAllTimersToStorage() {
        TimerController.SaveAllTimersToStorage();
    }


    public void CreateTimer() {
        String title_value = Title.getValue();
        int hours = NumberPicker_hours.getValue();
        int minutes = NumberPicker_minutes.getValue();
        int seconds = NumberPicker_seconds.getValue();
        int selected_radioButton_id = RadioGroup_saveType.getValue();

        AlarmTimer.ALARMTIMER_SAVE_TYPE saveTimerType = (selected_radioButton_id == R.id.radioButton_saveOrSingle_save) ? AlarmTimer.ALARMTIMER_SAVE_TYPE.SAVE : AlarmTimer.ALARMTIMER_SAVE_TYPE.SINGLE_USE;

        int lengthInSeconds = 0;
        lengthInSeconds += ((hours * 60) * 60);
        lengthInSeconds += (minutes * 60);
        lengthInSeconds += seconds;

        AlarmTimer alarmTimer = TimerController.CreateTimer(title_value, lengthInSeconds, saveTimerType);

        // Save timers to storage
        SaveAllTimersToStorage();
    }

    public boolean ValidateData(boolean toggleErrors) {
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

    //#region Title
    public void TitleChanged() {
        boolean result = IsTitleValid();
        ToggleTitleError(result);
        ToggleButtonEnabled(ValidateData(false));
    }

    private boolean IsTitleValid() {
        String value = Title.getValue();
        if (value.length() <= 0) {
            return false;
        } else {
            return true;
        }
    }

    private void ToggleTitleError(boolean isValid) {
        if (!isValid) {
            title_error.setValue("Must provide title.");
        } else {
            if (title_error.getValue() != null) {
                title_error.setValue(null);
            }
        }
    }
    //#endregion

    //#region Timer length
    public void TimerLengthChanged() {
        Dictionary<String, Boolean> resultNumberpickers = IsNumberpickersValid();
        ToggleNumberpickerErrors(resultNumberpickers);
        ToggleButtonEnabled(ValidateData(false));
    }

    private Dictionary<String, Boolean> IsNumberpickersValid() {
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

    private void ToggleNumberpickerErrors(Dictionary<String, Boolean> errors) {
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
    //#endregion

    //#region Save or Single
    public void SaveOrSingleChanged() {
        boolean result = IsRadiogroupValid();
        ToggleRadiogroupError(result);
        ToggleButtonEnabled(ValidateData(false));
    }

    private boolean IsRadiogroupValid() {
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

    private void ToggleRadiogroupError(boolean isValid) {
        if (!isValid) {
            saveOrSingle_error.setValue("Must choose timer type.");
        } else {
            // Remove err
            if (saveOrSingle_error.getValue() != null) {
                saveOrSingle_error.setValue(null);
            }
        }
    }
    //#endregion

    //#region Create button
    public void ToggleButtonEnabled(boolean enable) {
        this.btn_clickable.setValue(enable);
    }
    //#endregion
}
