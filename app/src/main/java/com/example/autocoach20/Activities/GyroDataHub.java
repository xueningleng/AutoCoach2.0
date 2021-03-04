package com.example.autocoach20.Activities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class GyroDataHub extends HardwareDataHub {
    private final static String UPDATE_COMMAND = "UPDATE";
    private final static int DELAY = 100; // 30ms between data points

    private final ReentrantLock queueLock = new ReentrantLock();
    private final List<String> rawData = new ArrayList<>();
    private final int queueSize;

    private Thread t = null;

    public GyroDataHub(int queueSize) {
        this.queueSize = queueSize;
    }

    /**
     * Start new thread to:
     * 1. Connect to server
     * 2. Fetch data at regular interval and add to queue.
     * 3. Remove first element if size becomes too large
     *
     * @param host
     * @param port
     * @return
     */
    public void run(String host, int port) {
        if (t != null)
            return;

        // Start thread
        t = new Thread(() -> {
            boolean success = super.connect(host, port);

            while (true) {
                String response = super.sendCommandAndWaitResponse(UPDATE_COMMAND);
                if (response == null)
                    continue;

                response = response.replace(',', '\n');

                // Add data to queue
                queueLock.lock();

                // Remove first element if the queue is full
                if (rawData.size() == queueSize) {
                    rawData.remove(0);
                }

                rawData.add(response);

                queueLock.unlock();

                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
    }

    public String getLastValue(){
        queueLock.lock();

        if (rawData.size() == 0) {
            queueLock.unlock();
            return null;
        }

        String val = rawData.get(rawData.size() - 1);
        queueLock.unlock();

        return val;
    }
}
