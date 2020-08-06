package com.funkyqubits.kitchentimer.ui.timers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.funkyqubits.kitchentimer.Controller.AlarmManagerController
import com.funkyqubits.kitchentimer.Controller.TimerController
import com.funkyqubits.kitchentimer.Repositories.IFileSystemRepository
import com.funkyqubits.kitchentimer.Repositories.ISharedPreferencesRepository

class TimersViewModelFactory (
        repository: IFileSystemRepository,
        timerOffsets: ISharedPreferencesRepository,
        private val alarmManagerController: AlarmManagerController
        ) : ViewModelProvider.Factory {

    private val timerController : TimerController = TimerController.Instance(repository, timerOffsets);

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimersViewModel::class.java)) {
            return TimersViewModel(timerController, alarmManagerController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}