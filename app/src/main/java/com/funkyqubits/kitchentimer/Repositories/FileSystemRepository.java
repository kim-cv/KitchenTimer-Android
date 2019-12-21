package com.funkyqubits.kitchentimer.Repositories;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;

import com.funkyqubits.kitchentimer.models.AlarmTimer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class FileSystemRepository implements IFileSystemRepository {
    private Context Context;
    private String Filename;
    private String FileEncoding = "UTF-8";


    public FileSystemRepository(Context _context, String _filename) {
        Context = _context;
        Filename = _filename;
    }


    @Override
    public ArrayList<AlarmTimer> LoadAlarmTimers() {
        InputStreamReader inputStreamReader = RetrieveTimersStreamReader();
        if (inputStreamReader == null) {
            // TODO handle
            return new ArrayList<>();
        }
        return MapJsonToAlarmTimers(inputStreamReader);
    }


    @Override
    public void SaveAlarmTimers(ArrayList<AlarmTimer> alarmTimers) {
        FileOutputStream fileOutputStream = CreateFileOutputStream();
        if (fileOutputStream == null) {
            // TODO handle
        }

        WriteToOutputStream(fileOutputStream, alarmTimers);
    }


    //#region Saving
    private FileOutputStream CreateFileOutputStream() {
        try {
            return Context.openFileOutput(Filename, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void WriteToOutputStream(FileOutputStream fileOutputStream, ArrayList<AlarmTimer> alarmTimers) {
        try {
            // Init a JSONWriter
            JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream, FileEncoding));
            jsonWriter.setIndent("    ");
            jsonWriter.beginArray();

            // Loop and write all AlarmTimers to the JSONWriter
            for (AlarmTimer alarmTimer : alarmTimers) {
                WriteAlarmTimerToJsonWriter(jsonWriter, alarmTimer);
            }

            // Close writers and streams
            jsonWriter.endArray();
            jsonWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write AlarmTimer to JSON
     *
     * @param jsonWriter
     * @param alarmTimer
     */
    private void WriteAlarmTimerToJsonWriter(JsonWriter jsonWriter, AlarmTimer alarmTimer) {
        try {
            jsonWriter.beginObject();
            jsonWriter.name("id").value(Integer.toString(alarmTimer.ID));
            jsonWriter.name("title").value(alarmTimer.Title);
            jsonWriter.name("time").value(Integer.toString(alarmTimer.LengthInSeconds));
            jsonWriter.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //#endregion

    //#region Loading

    /**
     * Returns input stream reader from file
     *
     * @return InputStreamReader
     */
    private InputStreamReader RetrieveTimersStreamReader() {
        // Get file stream and construct input stream reader
        try {
            FileInputStream fileInputStream = Context.openFileInput(Filename);
            return new InputStreamReader(fileInputStream, FileEncoding);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reads all data in input stream reader and maps to alarm timer object
     *
     * @param inputStreamReader
     * @return ArrayList<AlarmTimer>
     */
    public ArrayList<AlarmTimer> MapJsonToAlarmTimers(InputStreamReader inputStreamReader) {
        ArrayList<AlarmTimer> tmpArrayList = new ArrayList<>();

        try {
            JsonReader jsonReader = new JsonReader(inputStreamReader);

            // Loop through JSON
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                AlarmTimer alarmTimer = ReadJsonObject(jsonReader);
                if (alarmTimer == null) {
                    // If got null, continue to next iteration
                    continue;
                }
                tmpArrayList.add(alarmTimer);
            }
            jsonReader.endArray();

            jsonReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tmpArrayList;
    }

    /**
     * Reads JSON Object name and value and creates an AlarmTimer
     *
     * @param jsonReader
     * @return AlarmTimer or null
     */
    private AlarmTimer ReadJsonObject(JsonReader jsonReader) {
        AlarmTimer tmpAlarmTimer = null;

        try {
            jsonReader.beginObject();
            // Loop through key/value pairs of object
            while (jsonReader.hasNext()) {
                String tmpId = ReadJsonProperty(jsonReader, "id");
                String tmpTitle = ReadJsonProperty(jsonReader, "title");
                String tmpTime = ReadJsonProperty(jsonReader, "time");

                int id;
                String title;
                int time;

                // Make sure we got ID
                if (tmpId == null) {
                    id = -1;
                } else {
                    id = Integer.parseInt(tmpId);
                }

                // Make sure we got title
                if (tmpTitle == null) {
                    continue;
                } else {
                    title = tmpTitle;
                }

                // Make sure we got time
                if (tmpTime == null) {
                    continue;
                } else {
                    time = Integer.parseInt(tmpTime);
                }

                tmpAlarmTimer = new AlarmTimer(id, title, time, AlarmTimer.ALARMTIMER_SAVE_TYPE.SAVE);
            }
            jsonReader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tmpAlarmTimer;
    }

    /**
     * If next json property name matches parameter then returns json value otherwise null
     *
     * @param jsonReader
     * @param propertyName
     * @return
     */
    private String ReadJsonProperty(JsonReader jsonReader, String propertyName) {
        try {
            String nextPropertyName = jsonReader.nextName();
            if (nextPropertyName.equals(propertyName)) {
                return jsonReader.nextString();
            } else {
                jsonReader.skipValue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //#endregion
}
