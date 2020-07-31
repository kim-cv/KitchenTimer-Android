package com.funkyqubits.kitchentimer.ui.add_timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.funkyqubits.kitchentimer.Controller.TimerController
import com.funkyqubits.kitchentimer.Repositories.IFileSystemRepository
import com.funkyqubits.kitchentimer.Repositories.ISharedPreferencesRepository

class AddTimerViewModelFactory (
        repository: IFileSystemRepository,
        timerOffsets: ISharedPreferencesRepository,
        private val alarmTimerId: Int

) : ViewModelProvider.Factory {

    private val timerController : TimerController = TimerController.Instance(repository, timerOffsets);

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTimerViewModel::class.java)) {
            return AddTimerViewModel(timerController, alarmTimerId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}