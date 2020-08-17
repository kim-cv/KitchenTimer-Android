package com.funkyqubits.kitchentimer.Interfaces

interface IAlarmTimerDialogSubject {
    fun RegisterObserver(observer: IAlarmTimerDialogObserver?)

    fun RemoveObserver(observer: IAlarmTimerDialogObserver?)

    fun NotifyOfDialogAlarmTimerReset(alarmTimerID: Int)

    fun NotifyOfDialogAlarmTimerEdit(alarmTimerID: Int)

    fun NotifyOfDialogAlarmTimerDelete(alarmTimerID: Int)
}