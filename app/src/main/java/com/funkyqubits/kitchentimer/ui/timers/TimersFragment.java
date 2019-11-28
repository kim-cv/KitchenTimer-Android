package com.funkyqubits.kitchentimer.ui.timers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.funkyqubits.kitchentimer.R;

public class TimersFragment extends Fragment {

    private TimersViewModel timersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        timersViewModel =
                ViewModelProviders.of(this).get(TimersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_timers, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        timersViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}