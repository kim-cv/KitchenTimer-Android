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

import com.funkyqubits.kitchentimer.Controller.TimerController;
import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.Repositories.FileSystemRepository;
import com.funkyqubits.kitchentimer.Repositories.IFileSystemRepository;
import com.funkyqubits.kitchentimer.databinding.FragmentAddTimerBinding;

import java.util.Dictionary;

public class AddTimerFragment extends Fragment {

    private int parameter_timerId;

    private AddTimerViewModel addTimerViewModel;
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
        btn_create = root.findViewById(R.id.btn_addTimer_create);

        // Button create listener
        /*
        btn_create.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidateViewData(true)) {
                    CreateTimer();
                }
            }
        });*/

        // Title text change listener
        addTimerViewModel.Title.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String title) {
                boolean result = addTimerViewModel.IsTitleValid();
                addTimerViewModel.ToggleTitleError(result);
                addTimerViewModel.ToggleButtonEnabled(addTimerViewModel.ValidateViewData(false));
            }
        });

        // Timer length change listener
        addTimerViewModel.NumberPicker_hours.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer number) {
                Dictionary<String, Boolean> resultNumberpickers = addTimerViewModel.IsNumberpickersValid();
                addTimerViewModel.ToggleNumberpickerErrors(resultNumberpickers);
                addTimerViewModel.ToggleButtonEnabled(addTimerViewModel.ValidateViewData(false));
            }
        });
        addTimerViewModel.NumberPicker_minutes.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer number) {
                Dictionary<String, Boolean> resultNumberpickers = addTimerViewModel.IsNumberpickersValid();
                addTimerViewModel.ToggleNumberpickerErrors(resultNumberpickers);
                addTimerViewModel.ToggleButtonEnabled(addTimerViewModel.ValidateViewData(false));
            }
        });
        addTimerViewModel.NumberPicker_seconds.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer number) {
                Dictionary<String, Boolean> resultNumberpickers = addTimerViewModel.IsNumberpickersValid();
                addTimerViewModel.ToggleNumberpickerErrors(resultNumberpickers);
                addTimerViewModel.ToggleButtonEnabled(addTimerViewModel.ValidateViewData(false));
            }
        });

        // Timer type change listener
        addTimerViewModel.RadioGroup_saveType.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer number) {
                boolean result = addTimerViewModel.IsRadiogroupValid();
                addTimerViewModel.ToggleRadiogroupError(result);
                addTimerViewModel.ToggleButtonEnabled(addTimerViewModel.ValidateViewData(false));
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean result = addTimerViewModel.ValidateViewData(false);
        addTimerViewModel.ToggleButtonEnabled(result);
    }


    private void CreateTimer() {
        String editText_title_value = addTimerViewModel.Title.getValue();
        int numberPicker_hours_value = addTimerViewModel.NumberPicker_hours.getValue();
        int numberPicker_minutes_value = addTimerViewModel.NumberPicker_minutes.getValue();
        int numberPicker_seconds_value = addTimerViewModel.NumberPicker_seconds.getValue();
        int selected_radioButton_id = addTimerViewModel.RadioGroup_saveType.getValue();

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
        //addTimerViewModel.CreateTimer(editText_title_value, numberPicker_hours_value, numberPicker_minutes_value, numberPicker_seconds_value, shouldSaveTimer);

        // Save timers to storage
        addTimerViewModel.SaveAllTimersToStorage();
    }

}
