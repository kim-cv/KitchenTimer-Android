package com.funkyqubits.kitchentimer.ui.add_timer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddTimerViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public AddTimerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is add timers fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
