package com.example.autocoach20.Activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This data hub is used to exchange commands with server. All operations are blocking.
 */
public class HardwareDataHub {
    private String host;
    private int port;
    private Socket socket;
    private boolean connected;

    private PrintWriter out;
    private BufferedReader in;

    public HardwareDataHub() {
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean connect(String host, int port) {
        // If connected, close old connection
        if (connected)
            close();

        this.host = host;
        this.port = port;

        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
        } catch (IOException e) {
            close();
        }

        return connected;
    }

    public void close() {
        host = null;

        port = -1;

        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {

            }
            in = null;
        }

        if (out != null) {
            out.close();
            out = null;
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {

            }
            socket = null;
        }

        connected = false;
    }


    public String sendCommandAndWaitResponse(String command) {
        // Make sure datahub is connected
        if (!connected)
            throw new IllegalStateException("Calling sendCommand on non-connected HardwareDataHub is not allowed");

        // Send command
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
