package com.funkyqubits.kitchentimer.ui.timers

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerDialogObserver
import com.funkyqubits.kitchentimer.Interfaces.IAlarmTimerDialogSubject
import com.funkyqubits.kitchentimer.R
import com.funkyqubits.kitchentimer.databinding.TimerListItemOptionsBinding
import com.funkyqubits.kitchentimer.models.AlarmTimer


class TimerOptionsDialog(val alarmTimer: AlarmTimer, val fragment : Fragment) : DialogFragment(), IAlarmTimerDialogSubject {

    private val observers : MutableList<IAlarmTimerDialogObserver> = mutableListOf()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding: TimerListItemOptionsBinding = DataBindingUtil
                .inflate(LayoutInflater.from(context), R.layout.timer_list_item_options, null, false)

        binding.lifecycleOwner = fragment // Necessary for LiveData and MutableLiveData to work

        binding.alarmTimer = alarmTimer

        val dialog = AlertDialog.Builder(activity)
                .setView(binding.root)
                .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btn_timer_reset : Button = binding.root.findViewById(R.id.btn_timer_reset)
        val btn_timer_edit : Button = binding.root.findViewById(R.id.btn_timer_edit)
        val btn_timer_delete : Button = binding.root.findViewById(R.id.btn_timer_delete)

        btn_timer_reset.setOnClickListener {
            NotifyOfDialogAlarmTimerReset(alarmTimer.ID)
        }

        btn_timer_edit.setOnClickListener {
            NotifyOfDialogAlarmTimerEdit(alarmTimer.ID)
        }

        btn_timer_delete.setOnClickListener {
            NotifyOfDialogAlarmTimerDelete(alarmTimer.ID)
        }

        return dialog
    }

    override fun RegisterObserver(observer: IAlarmTimerDialogObserver) {
        if (!observers.contains(observer)) {
            observers.add(observer)
        }
    }

    override fun RemoveObserver(observer: IAlarmTimerDialogObserver) {
        if (observers.contains(observer)) {
            observers.remove(observer)
        }
    }

    override fun NotifyOfDialogAlarmTimerReset(alarmTimerID: Int) {
        for (observer in observers) {
            observer.OnDialogAlarmTimerReset(alarmTimerID)
        }
    }

    override fun NotifyOfDialogAlarmTimerEdit(alarmTimerID: Int) {
        for (observer in observers) {
            observer.OnDialogAlarmTimerEdit(alarmTimerID)
        }
    }

    override fun NotifyOfDialogAlarmTimerDelete(alarmTimerID: Int) {
        for (observer in observers) {
            observer.OnDialogAlarmTimerDelete(alarmTimerID)
        }
    }
}