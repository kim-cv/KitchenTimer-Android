package com.funkyqubits.kitchentimer.ui.add_timer;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

import com.funkyqubits.kitchentimer.R;

public class AddTimerFragment extends Fragment {

    private AddTimerViewModel addTimerViewModel;

    private EditText editText_title;
    private NumberPicker numberPicker_hours;
    private NumberPicker numberPicker_minutes;
    private NumberPicker numberPicker_seconds;
    private RadioGroup radioGroup;
    private Button btn_create;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addTimerViewModel =
                ViewModelProviders.of(this).get(AddTimerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add_timer, container, false);

        // Find views
        editText_title = root.findViewById(R.id.editText_title);
        numberPicker_hours = root.findViewById(R.id.numberPicker_hours);
        numberPicker_minutes = root.findViewById(R.id.numberPicker_minutes);
        numberPicker_seconds = root.findViewById(R.id.numberPicker_seconds);
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

        return root;
    }

    private boolean ValidateViewData() {
        String editText_title_value = editText_title.getText().toString();
        if (editText_title_value.length() <= 0) {
            return false;
        }

        int numberPicker_hours_value = numberPicker_hours.getValue();
        if (numberPicker_hours_value < 0 || numberPicker_hours_value > 23) {
            return false;
        }

        int numberPicker_minutes_value = numberPicker_minutes.getValue();
        if (numberPicker_minutes_value < 0 || numberPicker_minutes_value > 59) {
            return false;
        }

        int numberPicker_seconds_value = numberPicker_seconds.getValue();
        if (numberPicker_seconds_value < 0 || numberPicker_seconds_value > 59) {
            return false;
        }

        return true;
    }

    private void CreateTimer() {

    }

}
