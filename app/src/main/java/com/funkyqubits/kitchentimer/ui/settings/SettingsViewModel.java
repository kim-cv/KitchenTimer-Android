package com.funkyqubits.kitchentimer.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SettingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Nothing here yet.\n\nI just remade this app from scratch so I haven't had time to create settings. \uD83D\uDE42 \n\nIn the next updates this will contain some settings.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}