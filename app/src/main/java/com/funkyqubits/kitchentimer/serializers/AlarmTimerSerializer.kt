package com.funkyqubits.kitchentimer.serializers

import com.funkyqubits.kitchentimer.models.AlarmTimer
import com.google.gson.*

class AlarmTimerSerializer {
    companion object {
        fun serializer(): JsonSerializer<AlarmTimer> {
            return JsonSerializer { src, typeOfSrc, context ->
                val jsonObject = JsonObject()

                jsonObject.add("id", context.serialize(src.ID))
                jsonObject.add("title", context.serialize(src.Title))
                jsonObject.add("time", context.serialize(src.LengthInSeconds))

                jsonObject
            }
        }

        fun deserializer(): JsonDeserializer<ArrayList<AlarmTimer>> {
            return JsonDeserializer { json, typeOfT, context ->
                json as JsonArray

                val alarmTimers: ArrayList<AlarmTimer> = ArrayList()
                json.forEach {
                    it as JsonObject

                    val id = it.get("id").asInt
                    val title = it.get("title").asString
                    val lengthInSeconds = it.get("time").asInt

                    val alarmTimer = AlarmTimer(id, title, lengthInSeconds, AlarmTimer.ALARMTIMER_SAVE_TYPE.SAVE)
                    alarmTimers.add(alarmTimer)
                }

                alarmTimers
            }
        }
    }
}