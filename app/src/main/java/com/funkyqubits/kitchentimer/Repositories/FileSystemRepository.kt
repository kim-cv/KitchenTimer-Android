package com.funkyqubits.kitchentimer.Repositories

import android.content.Context
import com.funkyqubits.kitchentimer.models.AlarmTimer
import com.funkyqubits.kitchentimer.serializers.AlarmTimerSerializer
import com.google.gson.GsonBuilder
import java.io.File
import java.util.*

class FileSystemRepository(_context: Context, _filename: String) : IFileSystemRepository {
    private var Context: Context = _context
    private var Filename: String = _filename
    private val FileEncoding = Charsets.UTF_8

    override fun LoadAlarmTimers(): ArrayList<AlarmTimer> {
        val file = RetrieveFile()
        val fileContent = file.readText(FileEncoding)

        val alarmTimers: ArrayList<AlarmTimer> = ArrayList()
        if (!fileContent.isNullOrEmpty()) {
            val tmpAlarmTimers = fromJson(false, fileContent)
            alarmTimers.addAll(tmpAlarmTimers)
        }
        return alarmTimers
    }

    override fun SaveAlarmTimers(alarmTimers: ArrayList<AlarmTimer>) {
        val json = toJson(false, alarmTimers)
        val file = RetrieveFile()
        file.writeText(json, FileEncoding)
    }


    private fun fromJson(useKotlinSerializable: Boolean, json: String): ArrayList<AlarmTimer> {
        if (useKotlinSerializable) {
            throw NotImplementedError()
        } else {
            return gsonFromJson(json)
        }
    }

    private fun toJson(useKotlinSerializable: Boolean, alarmTimers: ArrayList<AlarmTimer>): String {
        if (useKotlinSerializable) {
            throw NotImplementedError()
        } else {
            return gsonToJson(alarmTimers)
        }
    }

    //#region Kotlin serialize
    //TODO Change from Google Gson to Kotlin Serialize, require rewrite of AlarmTimer to kotlin
    //#endregion

    //#region Gson
    private fun gsonFromJson(json: String): ArrayList<AlarmTimer> {
        val gSon = GsonBuilder().registerTypeAdapter(AlarmTimer::class.java, AlarmTimerSerializer.deserializer()).create()
        return gSon.fromJson<ArrayList<AlarmTimer>>(json, AlarmTimer::class.java)
    }

    private fun gsonToJson(alarmTimers: ArrayList<AlarmTimer>): String {
        val gSon = GsonBuilder().registerTypeAdapter(AlarmTimer::class.java, AlarmTimerSerializer.serializer()).create()
        return gSon.toJson(alarmTimers)
    }
    //#endregion

    private fun RetrieveFile(): File {
        val file = File(Context.filesDir, Filename)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }
}