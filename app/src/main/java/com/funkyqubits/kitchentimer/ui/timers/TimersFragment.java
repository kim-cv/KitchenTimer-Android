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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.Repositories.FileSystemRepository;
import com.funkyqubits.kitchentimer.Repositories.IRepository;

import java.util.ArrayList;

public class TimersFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;



    private TimersViewModel timersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        timersViewModel =
                ViewModelProviders.of(this).get(TimersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_timers, container, false);

        /*
        final TextView textView = root.findViewById(R.id.text_home);
        timersViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */

        // TODO: Figure out how to use dependency injection in Android MVVM
        IRepository repository = new FileSystemRepository(getContext(), "SavedTimings.json");
        timersViewModel.ProvideRepository(repository);


        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_alarmTimers);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        String[] dataset = new String[3];
        dataset[0] = "Hello";
        dataset[1] = "World";
        dataset[2] = "Awesome";
        mAdapter = new AlarmTimersAdapter(dataset);
        recyclerView.setAdapter(mAdapter);


        return root;
    }
}