package com.funkyqubits.kitchentimer.ui.add_timer;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.funkyqubits.kitchentimer.R;

public class AddTimerFragment extends Fragment {

    private AddTimerViewModel addTimerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addTimerViewModel =
                ViewModelProviders.of(this).get(AddTimerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add_timer, container, false);
        final TextView textView = root.findViewById(R.id.text_add_timer);
        addTimerViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

}
