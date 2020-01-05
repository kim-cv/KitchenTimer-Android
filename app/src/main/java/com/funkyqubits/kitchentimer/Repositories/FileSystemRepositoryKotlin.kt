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
        val file = RetrieveFile()
        val fileContent = file.readText(FileEncoding)

        val alarmTimers: ArrayList<AlarmTimer> = ArrayList()
        if (!fileContent.isNullOrEmpty()) {
            val gSon = GsonBuilder().registerTypeAdapter(AlarmTimer::class.java, AlarmTimerSerializer.deserializer()).create()
            val tmpAlarmTimers = gSon.fromJson<ArrayList<AlarmTimer>>(fileContent, AlarmTimer::class.java)
            alarmTimers.addAll(tmpAlarmTimers)
        }
        return alarmTimers
    }

    override fun SaveAlarmTimers(alarmTimers: ArrayList<AlarmTimer>) {
        val gSon = GsonBuilder().registerTypeAdapter(AlarmTimer::class.java, AlarmTimerSerializer.serializer()).create()
        val json = gSon.toJson(alarmTimers)
        val file = RetrieveFile()
        file.writeText(json, FileEncoding)
    }

    private fun RetrieveFile(): File {
        val file = File(Context.filesDir, Filename)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }
}