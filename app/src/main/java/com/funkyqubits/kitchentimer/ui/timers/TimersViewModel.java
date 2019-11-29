package com.funkyqubits.kitchentimer.ui.timers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.funkyqubits.kitchentimer.Controller.TimerController;
import com.funkyqubits.kitchentimer.Repositories.IRepository;

public class TimersViewModel extends ViewModel {

    private TimerController TimerController;
    private MutableLiveData<String> mText;

    public TimersViewModel() {
        /*
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
         */
    }

    public void ProvideRepository(IRepository _timerRepository){
        // TODO: Figure out how to use dependency injection in Android MVVM
        //TimerController = new TimerController(_timerRepository);
    }

    public LiveData<String> getText() {
        return mText;
    }
}