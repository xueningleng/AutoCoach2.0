package com.example.autocoach20.Activities;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HardwareDataHub {
    String host;
    int port;
    Socket socket;
    boolean connected;

    PrintWriter out;
    BufferedReader in;

    HardwareDataHub(String host, int port) {
        this.host = host;
        this.port = port;
    }

    boolean connect() {
        if (connected)
            return true;

        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
        } catch (IOException e) {
            connected = false;
        }

        return connected;
    }

    String sendCommand(String command) {
        // Try to connect to the server
        connect();

        // Dont continue if connection DNE
        if (!connected)
            return null;

        out.println(command);
        out.flush();

        String response;
        try {
            response = in.readLine();
        } catch (IOException e) {
            connected = false;
            return null;
        }

        return response;
    }
}
