package com.funkyqubits.kitchentimer.Repositories

import android.content.Context
import com.funkyqubits.kitchentimer.models.AlarmTimer
import com.funkyqubits.kitchentimer.serializers.AlarmTimerSerializer
import com.google.gson.GsonBuilder
import java.io.File
import java.util.*

class FileSystemRepositoryKotlin(_context: Context, _filename: String) : IFileSystemRepository {
    private var Context: Context = _context
    private var Filename: String = _filename
    private val FileEncoding = Charsets.UTF_8

    override fun LoadAlarmTimers(): ArrayList<AlarmTimer> {
        val file = File(Context.filesDir, Filename)
        val fileContent = file.readText(FileEncoding)
        val gSon = GsonBuilder().registerTypeAdapter(AlarmTimer::class.java, AlarmTimerSerializer.deserializer()).create()
        return gSon.fromJson<ArrayList<AlarmTimer>>(fileContent, AlarmTimer::class.java)
    }

    override fun SaveAlarmTimers(alarmTimers: ArrayList<AlarmTimer>) {
        val gSon = GsonBuilder().registerTypeAdapter(AlarmTimer::class.java, AlarmTimerSerializer.serializer()).create()
        val json = gSon.toJson(alarmTimers)
        val file = File(Context.filesDir, Filename)
        file.writeText(json, FileEncoding)
    }
}