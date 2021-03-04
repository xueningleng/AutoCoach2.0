package com.example.autocoach20.Activities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This data hub is used to fetch head position with server. All operations should be non-blocking.
 */
public class HeadPositionDataHub extends HardwareDataHub {
    private final static String UPDATE_COMMAND = "UPDATE";
    private final static int DELAY = 30; // 30ms between data points

    private final ReentrantLock queueLock = new ReentrantLock();
    private final List<Float> rawData = new ArrayList<>();
    private final int queueSize;
    private final float threshold;
    private Float frontAngle, leftAngle, rightAngle;
    private Thread t = null;

    public HeadPositionDataHub(int queueSize, float threshold) {
        this.queueSize = queueSize;
        this.threshold = Math.abs(threshold);
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

                String[] parts = response.split(",");
                if (parts.length != 2)
                    continue;

                if (Integer.parseInt(parts[1]) != 1)
                    continue;

                // Add data to queue
                queueLock.lock();

                // Remove first element if the queue is full
                if (rawData.size() == queueSize) {
                    rawData.remove(0);
                }

                rawData.add(Float.parseFloat(parts[0]));

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


    public void startFetchData() {
        // TODO: to be implemented
    }

    public void stopFetchData() {
        // TODO: to be implemented
    }

    public void clearQueue() {
        queueLock.lock();
        rawData.clear();
        queueLock.unlock();
    }

    public boolean queueIsFull() {
        queueLock.lock();
        int len = rawData.size();
        queueLock.unlock();
        return len == queueSize;
    }


    public void setRegularizationParam(float frontAngle, float leftAngle, float rightAngle) {
        queueLock.lock();
        this.frontAngle = frontAngle;
        this.leftAngle = leftAngle;
        this.rightAngle = rightAngle;
        queueLock.unlock();
    }

    /**
     * Get last regularized angle
     *
     * @return null if no data available or any regularization angles is null.
     * Else regularized angle range from (-100.0, 100.0)
     */
    public Float getLastRegularizedAngle() {
        queueLock.lock();

        if (rawData.size() == 0) {
            queueLock.unlock();
            return null;
        }

        if (frontAngle == null || leftAngle == null || rightAngle == null) {
            queueLock.unlock();
            return null;
        }


        // Use last value because it is the most recent
        float rawAngle = rawData.get(rawData.size() - 1);
        queueLock.unlock();

        System.out.println("Angle " + Float.toString(rawAngle));

        // Regularize angle and return
        if (rawAngle > frontAngle)
            return (rawAngle - frontAngle) / (rightAngle - frontAngle) * 100.0f;
        else
            return (frontAngle - rawAngle) / (frontAngle - leftAngle) * -100.0f;
    }

    public Direction getLastHeadDirection() {
        Float angle = getLastRegularizedAngle();

        if (angle == null)
            return Direction.NONE;

        if (angle < -threshold)
            return Direction.LEFT;

        if (angle > threshold)
            return Direction.RIGHT;

        return Direction.FRONT;
    }

    public Float getAveragedData() {
        queueLock.lock();

        if (rawData.size() == 0) {
            queueLock.unlock();
            return null;
        }

        int len = rawData.size();
        float sum = 0.0f;
        for (float v : rawData)
            sum += v;

        queueLock.unlock();

        return sum / len;
    }

    public Float getLastValue() {
        queueLock.lock();

        if (rawData.size() == 0) {
            queueLock.unlock();
            return null;
        }

        float val = rawData.get(rawData.size() - 1);
        queueLock.unlock();

        return val;
    }

    public enum Direction {
        NONE,
        FRONT,
        LEFT,
        RIGHT,
    }

}

