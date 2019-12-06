package com.funkyqubits.kitchentimer.ui.add_timer;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

import com.funkyqubits.kitchentimer.R;
import com.google.android.material.textfield.TextInputLayout;

public class AddTimerFragment extends Fragment {

    private AddTimerViewModel addTimerViewModel;

    private EditText editText_title;
    private TextInputLayout editText_title_textLayout;
    private TextInputLayout textView_timer_length_textLayout;
    private NumberPicker numberPicker_hours;
    private NumberPicker numberPicker_minutes;
    private NumberPicker numberPicker_seconds;
    private TextInputLayout textView_radioGroup_saveOrSingle_textLayout;
    private RadioGroup radioGroup;
    private Button btn_create;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addTimerViewModel =
                ViewModelProviders.of(this).get(AddTimerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add_timer, container, false);

        // Find views
        editText_title = root.findViewById(R.id.editText_title);
        editText_title_textLayout = root.findViewById(R.id.editText_title_textLayout);
        textView_timer_length_textLayout = root.findViewById(R.id.textView_timer_length_textLayout);
        numberPicker_hours = root.findViewById(R.id.numberPicker_hours);
        numberPicker_minutes = root.findViewById(R.id.numberPicker_minutes);
        numberPicker_seconds = root.findViewById(R.id.numberPicker_seconds);
        textView_radioGroup_saveOrSingle_textLayout = root.findViewById(R.id.textView_radioGroup_saveOrSingle_textLayout);
        radioGroup = root.findViewById(R.id.radioGroup_saveOrSingle);
        btn_create = root.findViewById(R.id.btn_addTimer_create);

        // Set number pickers min/max values
        numberPicker_hours.setMinValue(0);
        numberPicker_hours.setMaxValue(23);
        numberPicker_minutes.setMinValue(0);
        numberPicker_minutes.setMaxValue(59);
        numberPicker_seconds.setMinValue(0);
        numberPicker_seconds.setMaxValue(59);

        // Button create listener
        btn_create.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidateViewData()) {
                    CreateTimer();
                }
            }
        });

        // Title text change listener
        editText_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString();
                ValidateTitle(value);
            }
        });

        // Timer length change listener
        numberPicker_hours.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int minutes_value = numberPicker_minutes.getValue();
                int seconds_value = numberPicker_seconds.getValue();
                ValidateNumberpickers(newVal, minutes_value, seconds_value);
            }
        });
        numberPicker_minutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int hours_value = numberPicker_hours.getValue();
                int seconds_value = numberPicker_seconds.getValue();
                ValidateNumberpickers(hours_value, newVal, seconds_value);
            }
        });
        numberPicker_seconds.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int hours_value = numberPicker_hours.getValue();
                int minutes_value = numberPicker_minutes.getValue();
                ValidateNumberpickers(hours_value, minutes_value, newVal);
            }
        });

        // Timer type change listener
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ValidateRadiogroup(checkedId);
            }
        });

        return root;
    }

    private boolean ValidateViewData() {
        int numErrors = 0;

        String editText_title_value = editText_title.getText().toString();
        if (ValidateTitle(editText_title_value) == false) {
            numErrors += 1;
        }

        int numberPicker_hours_value = numberPicker_hours.getValue();
        int numberPicker_minutes_value = numberPicker_minutes.getValue();
        int numberPicker_seconds_value = numberPicker_seconds.getValue();
        if (ValidateNumberpickers(numberPicker_hours_value, numberPicker_minutes_value, numberPicker_seconds_value) == false) {
            numErrors += 1;
        }

        int selected_radioButton_id = radioGroup.getCheckedRadioButtonId();
        if (!ValidateRadiogroup(selected_radioButton_id)) {
            numErrors += 1;
        }

        if (numErrors > 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean ValidateTitle(String value) {
        if (value.length() <= 0) {
            editText_title_textLayout.setError("Must provide title.");
            return false;
        } else {
            if (editText_title_textLayout.getError() != null) {
                editText_title_textLayout.setError(null);
            }
            return true;
        }
    }

    private boolean ValidateNumberpickers(int hours, int minutes, int seconds) {
        boolean hours_range = (hours < 0 || hours > 23);
        boolean minutes_range = (minutes < 0 || minutes > 59);
        boolean seconds_range = (seconds < 0 || seconds > 59);
        boolean no_value = (hours <= 0 && minutes <= 0 && seconds <= 0);

        if (no_value) {
            textView_timer_length_textLayout.setError("Must choose timer length.");
            return false;
        } else if (hours_range) {
            textView_timer_length_textLayout.setError("Hours must be between 0 and 23.");
            return false;
        } else if (minutes_range) {
            textView_timer_length_textLayout.setError("Minutes must be between 0 and 59.");
            return false;
        } else if (seconds_range) {
            textView_timer_length_textLayout.setError("Seconds must be between 0 and 59.");
            return false;
        } else {
            // Remove err
            if (textView_timer_length_textLayout.getError() != null) {
                textView_timer_length_textLayout.setError(null);
            }
            return true;
        }
    }

    private boolean ValidateRadiogroup(int selected_radioButton_id) {
        switch (selected_radioButton_id) {
            case -1: {
                // Nothing selected
                textView_radioGroup_saveOrSingle_textLayout.setError("Must choose timer type.");
                return false;
            }
            default: {
                // Remove err
                if (textView_radioGroup_saveOrSingle_textLayout.getError() != null) {
                    textView_radioGroup_saveOrSingle_textLayout.setError(null);
                }
                return true;
            }
        }
    }

    private void CreateTimer() {

    }

}
