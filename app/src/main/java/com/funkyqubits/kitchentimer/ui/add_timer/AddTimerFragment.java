package com.funkyqubits.kitchentimer.ui.add_timer;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.funkyqubits.kitchentimer.R;

public class AddTimerFragment extends Fragment {

    private AddTimerViewModel addTimerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addTimerViewModel =
                ViewModelProviders.of(this).get(AddTimerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add_timer, container, false);

        final NumberPicker numberPicker_hours = root.findViewById(R.id.numberPicker_hours);
        final NumberPicker numberPicker_minutes = root.findViewById(R.id.numberPicker_minutes);
        final NumberPicker numberPicker_seconds = root.findViewById(R.id.numberPicker_seconds);

        numberPicker_hours.setMinValue(0);
        numberPicker_hours.setMaxValue(23);

        numberPicker_minutes.setMinValue(0);
        numberPicker_minutes.setMaxValue(59);

        numberPicker_seconds.setMinValue(0);
        numberPicker_seconds.setMaxValue(59);
        return root;
    }

}
