package com.funkyqubits.kitchentimer.ui.timers

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.funkyqubits.kitchentimer.R
import com.funkyqubits.kitchentimer.databinding.TimerListItemOptionsBinding
import com.funkyqubits.kitchentimer.models.AlarmTimer


class TimerOptionsDialog(val alarmTimer: AlarmTimer, val fragment : Fragment) : DialogFragment() {

    private lateinit var observer_reset : () -> Unit
    private lateinit var observer_edit : () -> Unit
    private lateinit var observer_delete : () -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding: TimerListItemOptionsBinding = DataBindingUtil
                .inflate(LayoutInflater.from(context), R.layout.timer_list_item_options, null, false)

        binding.lifecycleOwner = fragment // Necessary for LiveData and MutableLiveData to work

        binding.alarmTimer = alarmTimer

        val dialog = AlertDialog.Builder(activity)
                .setView(binding.root)
                .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val txt_timer_reset : TextView = binding.root.findViewById(R.id.txt_timer_reset)
        val txt_timer_edit : TextView = binding.root.findViewById(R.id.txt_timer_edit)
        val txt_timer_delete : TextView = binding.root.findViewById(R.id.txt_timer_delete)
        val btn_timer_reset : Button = binding.root.findViewById(R.id.btn_timer_reset)
        val btn_timer_edit : Button = binding.root.findViewById(R.id.btn_timer_edit)
        val btn_timer_delete : Button = binding.root.findViewById(R.id.btn_timer_delete)

        txt_timer_reset.setOnClickListener {
            observer_reset.invoke()
        }
        txt_timer_edit.setOnClickListener {
            observer_edit.invoke()
        }
        txt_timer_delete.setOnClickListener {
            observer_delete.invoke()
        }
        btn_timer_reset.setOnClickListener {
            observer_reset.invoke()
        }
        btn_timer_edit.setOnClickListener {
            observer_edit.invoke()
        }
        btn_timer_delete.setOnClickListener {
            observer_delete.invoke()
        }

        return dialog
    }

    fun OnReset(func: () -> Unit) {
        observer_reset = func
    }

    fun OnEdit(func: () -> Unit) {
        observer_edit = func
    }

    fun OnDelete(func: () -> Unit) {
        observer_delete = func
    }
}