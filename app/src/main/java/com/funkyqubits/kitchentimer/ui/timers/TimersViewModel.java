package com.funkyqubits.kitchentimer.ui.timers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TimersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}