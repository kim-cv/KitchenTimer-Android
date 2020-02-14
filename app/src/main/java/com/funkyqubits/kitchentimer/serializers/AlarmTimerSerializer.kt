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

                    val idJsonElement = it.get("id")
                    val titleJsonElement = it.get("title")
                    val lengthInSecondsJsonElement = it.get("time")

                    val id: Int
                    val title: String
                    val lengthInSeconds: Int

                    // Make sure we got ID
                    id = when (idJsonElement == null) {
                        true -> -1
                        false -> idJsonElement.asInt
                    }

                    // Make sure we got title
                    title = when (titleJsonElement == null) {
                        true -> "No title"
                        false -> titleJsonElement.asString
                    }

                    // Make sure we got time
                    lengthInSeconds = when (lengthInSecondsJsonElement == null) {
                        true -> 60
                        false -> convertLengthFormat(lengthInSecondsJsonElement)
                    }

                    val alarmTimer = AlarmTimer(id, title, lengthInSeconds, AlarmTimer.ALARMTIMER_SAVE_TYPE.SAVE)
                    alarmTimers.add(alarmTimer)
                }

                alarmTimers
            }
        }

        private fun convertLengthFormat (lengthInSecondsJsonElement: JsonElement): Int {
            val asString = lengthInSecondsJsonElement.asString

            // Check if old time format
            if (Regex("^\\d{2}:\\d{2}:\\d{2}$").matches(asString)) {
                // Is old format
                return mapOldLengthFormatToNewFormat(asString)
            } else {
                return lengthInSecondsJsonElement.asInt
            }
        }

        private fun mapOldLengthFormatToNewFormat(oldFormat: String): Int {
            val oldFormatParts = oldFormat.split(':')

            val hoursString = oldFormatParts[0]
            val minutesString = oldFormatParts[1]
            val secondsString = oldFormatParts[2]

            val hours = Integer.parseInt(hoursString)
            val minutes = Integer.parseInt(minutesString)
            val seconds = Integer.parseInt(secondsString)

            val timeLength = (hours * 60 * 60) + (minutes * 60) + seconds

            return timeLength
        }
    }
}