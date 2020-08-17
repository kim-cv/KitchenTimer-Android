package com.funkyqubits.kitchentimer.Interfaces

interface IAlarmTimerDialogObserver {
    fun OnDialogAlarmTimerReset(alarmTimerID: Int)

    fun OnDialogAlarmTimerEdit(alarmTimerID: Int)

    fun OnDialogAlarmTimerDelete(alarmTimerID: Int)
}