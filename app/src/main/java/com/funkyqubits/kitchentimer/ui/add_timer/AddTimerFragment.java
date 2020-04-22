package com.funkyqubits.kitchentimer.ui.add_timer;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.funkyqubits.kitchentimer.Controller.TimerController;
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerCreatedUpdatedObserver;
import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.Repositories.FileSystemRepository;
import com.funkyqubits.kitchentimer.Repositories.IFileSystemRepository;
import com.funkyqubits.kitchentimer.Repositories.ISharedPreferencesRepository;
import com.funkyqubits.kitchentimer.Repositories.SharedPreferencesRepository;
import com.funkyqubits.kitchentimer.databinding.FragmentAddTimerBinding;

public class AddTimerFragment extends Fragment implements IAlarmTimerCreatedUpdatedObserver {

    private int parameter_timerId = 0;

    private AddTimerViewModel addTimerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Get parameter
        if (getArguments() != null && getArguments().getInt(getString(R.string.parameter_timerId)) > 0) {
            parameter_timerId = getArguments().getInt(getString(R.string.parameter_timerId));
        }

        addTimerViewModel = new ViewModelProvider(this).get(AddTimerViewModel.class);

        // Inflate view and obtain an instance of the binding class.
        FragmentAddTimerBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_timer, container, false);
        binding.setViewmodel(addTimerViewModel);
        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.executePendingBindings();
        View root = binding.getRoot();

        // TODO: Figure out how to use dependency injection in Android MVVM
        IFileSystemRepository repository = new FileSystemRepository(getContext(), getString(R.string.file_timers));
        ISharedPreferencesRepository timerOffsets = new SharedPreferencesRepository(getContext());
        TimerController timerController = TimerController.Instance(repository, timerOffsets);
        addTimerViewModel.ProvideExtra(timerController, parameter_timerId);

        // Title text change listener
        addTimerViewModel.Title.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String title) {
                addTimerViewModel.TitleChanged();
            }
        });

        // Timer length change listener
        addTimerViewModel.NumberPicker_hours.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer number) {
                addTimerViewModel.TimerLengthChanged();
            }
        });
        addTimerViewModel.NumberPicker_minutes.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer number) {
                addTimerViewModel.TimerLengthChanged();
            }
        });
        addTimerViewModel.NumberPicker_seconds.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer number) {
                addTimerViewModel.TimerLengthChanged();
            }
        });

        // Timer type change listener
        addTimerViewModel.RadioGroup_saveType.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer number) {
                addTimerViewModel.SaveOrSingleChanged();
            }
        });

        addTimerViewModel.RegisterObserver(this);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean result = addTimerViewModel.IsDataValid(false);
        addTimerViewModel.ToggleButtonEnabled(result);
    }

    @Override
    public void OnAlarmTimerCreated(int alarmTimerID) {
        NavController navController = Navigation.findNavController(this.getView());
        navController.navigate(R.id.navigation_timers);
    }

    @Override
    public void OnAlarmTimerUpdated(int alarmTimerID) {
        NavController navController = Navigation.findNavController(this.getView());
        navController.navigate(R.id.navigation_timers);
    }
}
