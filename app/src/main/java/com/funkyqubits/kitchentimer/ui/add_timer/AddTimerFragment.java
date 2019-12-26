package com.funkyqubits.kitchentimer.ui.add_timer;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.funkyqubits.kitchentimer.Controller.TimerController;
import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.Repositories.FileSystemRepository;
import com.funkyqubits.kitchentimer.Repositories.IFileSystemRepository;
import com.funkyqubits.kitchentimer.databinding.FragmentAddTimerBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Dictionary;
import java.util.Hashtable;

public class AddTimerFragment extends Fragment {

    private int parameter_timerId;

    private AddTimerViewModel addTimerViewModel;

    private TextInputLayout editText_title_textLayout;
    private TextInputLayout textView_timer_length_textLayout;
    private TextInputLayout textView_radioGroup_saveOrSingle_textLayout;
    private RadioGroup radioGroup;
    private Button btn_create;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Get parameter
        if (getArguments() != null && getArguments().getInt(getString(R.string.parameter_timerId)) > 0) {
            parameter_timerId = getArguments().getInt(getString(R.string.parameter_timerId));
        }

        addTimerViewModel = ViewModelProviders.of(this).get(AddTimerViewModel.class);

        // Inflate view and obtain an instance of the binding class.
        FragmentAddTimerBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_timer, container, false);
        binding.setViewmodel(addTimerViewModel);
        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.executePendingBindings();
        View root = binding.getRoot();

        // TODO: Figure out how to use dependency injection in Android MVVM
        IFileSystemRepository repository = new FileSystemRepository(getContext(), getString(R.string.file_timers));
        TimerController timerController = TimerController.Instance(repository);
        addTimerViewModel.ProvideExtra(timerController);

        // Find views
        editText_title_textLayout = root.findViewById(R.id.editText_title_textLayout);
        textView_timer_length_textLayout = root.findViewById(R.id.textView_timer_length_textLayout);
        textView_radioGroup_saveOrSingle_textLayout = root.findViewById(R.id.textView_radioGroup_saveOrSingle_textLayout);
        radioGroup = root.findViewById(R.id.radioGroup_saveOrSingle);
        btn_create = root.findViewById(R.id.btn_addTimer_create);

        // Button create listener
        btn_create.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidateViewData(true)) {
                    CreateTimer();
                }
            }
        });

        // Title text change listener
        addTimerViewModel.Title.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String title) {
                boolean result = IsTitleValid();
                ToggleTitleError(result);
                ToggleButtonEnabled(ValidateViewData(false));
            }
        });

        // Timer length change listener
        addTimerViewModel.NumberPicker_hours.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer number) {
                Dictionary<String, Boolean> resultNumberpickers = IsNumberpickersValid();
                ToggleNumberpickerErrors(resultNumberpickers);
                ToggleButtonEnabled(ValidateViewData(false));
            }
        });
        addTimerViewModel.NumberPicker_minutes.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer number) {
                Dictionary<String, Boolean> resultNumberpickers = IsNumberpickersValid();
                ToggleNumberpickerErrors(resultNumberpickers);
                ToggleButtonEnabled(ValidateViewData(false));
            }
        });
        addTimerViewModel.NumberPicker_seconds.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer number) {
                Dictionary<String, Boolean> resultNumberpickers = IsNumberpickersValid();
                ToggleNumberpickerErrors(resultNumberpickers);
                ToggleButtonEnabled(ValidateViewData(false));
            }
        });

        // Timer type change listener
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean result = IsRadiogroupValid();
                ToggleRadiogroupError(result);
                ToggleButtonEnabled(ValidateViewData(false));
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean result = ValidateViewData(false);
        ToggleButtonEnabled(result);
    }

    private boolean ValidateViewData(boolean toggleErrors) {
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

    private void ToggleButtonEnabled(boolean enable) {
        int color;
        if (enable) {
            color = getResources().getColor(R.color.colorMain);
        } else {
            color = getResources().getColor(R.color.colorSecondary);
        }

        Drawable buttonDrawable = btn_create.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, color);
        btn_create.setBackground(buttonDrawable);
    }

    private boolean IsTitleValid() {
        String value = addTimerViewModel.Title.getValue();
        if (value.length() <= 0) {
            return false;
        } else {
            return true;
        }
    }

    private void ToggleTitleError(boolean isValid) {
        if (!isValid) {
            editText_title_textLayout.setError("Must provide title.");
        } else {
            if (editText_title_textLayout.getError() != null) {
                editText_title_textLayout.setError(null);
            }
        }
    }

    private Dictionary<String, Boolean> IsNumberpickersValid() {
        Hashtable errors = new Hashtable();

        int hours = addTimerViewModel.NumberPicker_hours.getValue();
        int minutes = addTimerViewModel.NumberPicker_minutes.getValue();
        int seconds = addTimerViewModel.NumberPicker_seconds.getValue();

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
            textView_timer_length_textLayout.setError("Must choose timer length.");
        } else if (!errors.get("hours")) {
            textView_timer_length_textLayout.setError("Hours must be between 0 and 23.");
        } else if (!errors.get("minutes")) {
            textView_timer_length_textLayout.setError("Minutes must be between 0 and 59.");
        } else if (!errors.get("seconds")) {
            textView_timer_length_textLayout.setError("Seconds must be between 0 and 59.");
        } else {
            // Remove err
            if (textView_timer_length_textLayout.getError() != null) {
                textView_timer_length_textLayout.setError(null);
            }
        }
    }

    private boolean IsRadiogroupValid() {
        int selected_radioButton_id = radioGroup.getCheckedRadioButtonId();
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
            textView_radioGroup_saveOrSingle_textLayout.setError("Must choose timer type.");
        } else {
            // Remove err
            if (textView_radioGroup_saveOrSingle_textLayout.getError() != null) {
                textView_radioGroup_saveOrSingle_textLayout.setError(null);
            }
        }
    }

    private void CreateTimer() {
        String editText_title_value = addTimerViewModel.Title.getValue();
        int numberPicker_hours_value = addTimerViewModel.NumberPicker_hours.getValue();
        int numberPicker_minutes_value = addTimerViewModel.NumberPicker_minutes.getValue();
        int numberPicker_seconds_value = addTimerViewModel.NumberPicker_seconds.getValue();
        int selected_radioButton_id = radioGroup.getCheckedRadioButtonId();

        boolean shouldSaveTimer;
        switch (selected_radioButton_id) {
            case R.id.radioButton_saveOrSingle_save: {
                shouldSaveTimer = true;
                break;
            }
            case R.id.radioButton_saveOrSingle_single: {
                shouldSaveTimer = false;
                break;
            }
            default: {
                shouldSaveTimer = true;
                break;
            }
        }
        addTimerViewModel.CreateTimer(editText_title_value, numberPicker_hours_value, numberPicker_minutes_value, numberPicker_seconds_value, shouldSaveTimer);

        // Save timers to storage
        addTimerViewModel.SaveAllTimersToStorage();
    }

}
