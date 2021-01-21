package com.example.autocoach20.Activities.EventsCache;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.autocoach20.Activities.SyncServices.Bean.Event;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class EventsCache {
    File cacheDir;
    SharedPreferences sharedpreferences;
    final String preferenceFileKey = "AutoCoach";
    Context appContext;

    public EventsCache(Context appContext) {
        this.appContext= appContext;
        this.cacheDir = appContext.getCacheDir();
    }

    public void storeDict(String key, String value) {
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(
                this.preferenceFileKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply(); //this will store the value in the background.
        //editor.commit() will store it immediately. Will block the thread. Not preferred.
    }

    public String loadDict(String key) {
        SharedPreferences sharedPref = this.appContext.getSharedPreferences(this.preferenceFileKey, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public boolean testDict(){
        String testingValue = "Test the functionality";
        this.storeDict("testing1","Test the functionality");
        return this.loadDict("testing1").equals(testingValue);
    }

    private String write(String filename, String fileContent) throws IOException {
        assert this.cacheDir.isDirectory();
        assert this.cacheDir.exists();
        File f = File.createTempFile(filename, null, this.cacheDir);
        FileWriter fileWriter = new FileWriter(f);
        fileWriter.write(fileContent);
        fileWriter.flush();
        fileWriter.close();
        assert f.exists();
        Log.i("Cache", "Written to " + f.getAbsolutePath());
        return f.getName();
    }

    private String read(String filename) throws IOException {
        File f = new File(this.cacheDir, filename);

        StringBuilder result = new StringBuilder();
        Scanner myReader = new Scanner(f);
        while (myReader.hasNextLine()) {
            result.append("\n").append(myReader.nextLine());
        }
        myReader.close();
        return result.toString();
    }

    public String saveEvent(Event event) {
        String result = Long.toString(System.currentTimeMillis());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String jsonString = gson.toJson(event);
        try {
            result = write("Event_" + result + "_", jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Cache", "Event saved to " + result);
        return result;
    }

    public Event loadEvent(String name) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Log.d("Cache", "Reading from " + name);
        String jsonString = read(name);
        return gson.fromJson(jsonString, Event.class);
    }
}
