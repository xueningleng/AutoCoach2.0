package com.example.autocoach20.Activities;

import java.io.File;

//This class is used to download the file and store it here
//Run application on Emulator or physical phone
//Search for Device File Explorer
//Go to sdcard > Android > data > com.example.autocoach > files ..
//The downloaded files from the server will be stored over there
public class FileUtils {
    private String path = "";

    public FileUtils(String path) {
        this.path = path;
        File file = new File(path);

        if (!file.exists()) {
            System.out.println("folder not exist");
        }
    }

    public File createFile(String FileName) {
        return new File(path, FileName);
    }
}
