package com.funkyqubits.kitchentimer.ui.timers

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.funkyqubits.kitchentimer.R
import com.funkyqubits.kitchentimer.databinding.TimerListItemOptionsBinding
import com.funkyqubits.kitchentimer.models.AlarmTimer


class TimerOptionsDialog(val alarmTimer: AlarmTimer, val fragment : Fragment) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding: TimerListItemOptionsBinding = DataBindingUtil
                .inflate(LayoutInflater.from(context), R.layout.timer_list_item_options, null, false)

        binding.lifecycleOwner = fragment // Necessary for LiveData and MutableLiveData to work

        binding.alarmTimer = alarmTimer

        return AlertDialog.Builder(activity)
                .setView(binding.root)
                .create()
    }
}