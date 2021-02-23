package com.example.autocoach20.Activities;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class HeadPositionDataHub {
    final static String UPDATE_COMMAND = "UPDATE";
    final static int DELAY = 30; // 30ms between data points

    HardwareDataHub hwdh;
    List<Float> rawData;
    Float frontAngle, leftAngle, rightAngle;

    HeadPositionDataHub(String host, int port) {
        hwdh = new HardwareDataHub(host, port);

        rawData = new ArrayList<>();
    }


    boolean update() {
        try {
            String response = hwdh.sendCommand(UPDATE_COMMAND);
            if (response == null)
                return false;

            String[] parts = response.split(",");
            if (parts.length != 2)
                return false;

            if (Integer.parseInt(parts[1]) != 1)
                return false;

            rawData.add(Float.parseFloat(parts[0]));

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    Float sampleAndAverage() {
        return sampleAndAverage(15);
    }

    Float sampleAndAverage(int samplingTime) {
        rawData.clear();

        long endMilli = new Date().getTime() + samplingTime * 1000;

        while (new Date().getTime() < endMilli) {
            update();
            SystemClock.sleep(DELAY);
        }

        if (rawData.size() == 0)
            return null;

        return averageRawData();
    }

    float averageRawData() {
        if (rawData.size() == 0)
            return 0.0f;

        float sum = 0.0f;
        for (float f : rawData)
            sum += f;

        return sum / rawData.size();
    }

    void setRegularizationParam(float frontAngle, float leftAngle, float rightAngle) {
        this.frontAngle = frontAngle;
        this.leftAngle = leftAngle;
        this.rightAngle = rightAngle;
    }

    Float getLastRegularizedAngle(int numRetries) {
        if (this.frontAngle == null)
            return null;

        rawData.clear();

        for (int i = 0; i < numRetries; i++) {
            if (!update()) continue;

            // Use last value because it is the most recent
            float rawAngle = rawData.get(rawData.size()-1);

            if(rawAngle>frontAngle)
                return (rawAngle-frontAngle)/(rightAngle-frontAngle)*100.0f;
            else
                return (frontAngle-rawAngle)/(frontAngle-leftAngle)*-100.0f;
        }

        return null;
    }
}
