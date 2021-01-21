package com.example.autocoach20.Activities.SyncServices.EventDetection;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.autocoach20.Activities.Databases.SensorDatabase.SensorContract;
import com.example.autocoach20.Activities.StartAutoCoachActivity;
import com.example.autocoach20.Activities.SyncServices.Bean.Event;
import com.google.common.collect.EvictingQueue;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilterCoefficients;
import biz.source_code.dsp.filter.IirFilterDesignExstrom;

/**
 * This is a service class which works in the background to detect and classify events
 * Then send the detected event information to the Main Activity in order to score and display
 * information related to the detected event.
 * @author Daben Wang and Zehua Wang
 * @version 1.0
 * @since Version 1.0
 */
public class SensorDetectService extends Service implements SensorEventListener {

    // *************************************************************** //
    // FIELDS
    // *************************************************************** //

    private SensorManager mSensorManager;       //Android Sensor Manager
    private LocationManager locationManager;    //GPS
    private Sensor mAccelerometer;              //Accelerometer Sensor
    private Sensor mGyroscope;                  //Gyroscope Sensor
    private Sensor mMagnetometer;               //Magnetometer Sensor

    private int acc_count = 0;                  //Flag Accelerometer
    private int gyo_count = 0;                  //Flag for Gyroscope

    public ArrayList<ContentValues> values = new ArrayList<>(); //This content values store all values of sensor which will be sent ot the database
    ContentValues value = new ContentValues();                  //This variable stores information of one record of data which will be stored in SensorDB

    //The trip ID is used to identify data belongs to which trip
    int trip_id = StartAutoCoachActivity.mainActivity.getDBTripId();

    private Intent broadcast_intent = new Intent("com.test.service.RECEIVER");
    private Lock dataLock = new ReentrantLock(true);        //This lock is used when data is being read or written

    // *************************************************************** //
    // EVENT DETECTION FIELDS
    // *************************************************************** //

    private Event accEvent = new Event(0,0);
    private Event brakeEvent = new Event(0,1);
    private Event lturnEvent = new Event(0, 2);
    private Event rturnEvent = new Event(0,2);
    private int samplingRate = 33;  //this is the sampling rate of sensor--33Hz
    private int stdWin = samplingRate;  //window size for standard deviation calculation
    private IirFilterCoefficients iirFilterCoefficients = IirFilterDesignExstrom.design(FilterPassType.lowpass, 5,
            10.0/340, 10.0 / 170);  //filter for low pass filter
    private Queue<double[]> dataQueue = EvictingQueue.create(120);
    private Queue<Double> stdXQueue = EvictingQueue.create(40);
    private Queue<Double> stdYQueue = EvictingQueue.create(40);

    private int accEventDataNum = 0;
    private int brakeEventDataNum = 0;
    private int lturnEventDataNum = 0;
    private int rturnEventDataNum = 0;
    private boolean yPositive = true;
    private boolean yNegative = true;
    private int accFault = 5;
    private int brakeFault = 5;
    private int lturnFault = 5;
    private int rturnFault = 5;

    private boolean isCalibrate = true;
    private boolean isCalibrateEnd = true;
    private int calibrateNum = 40;
    private double meanX = 0;
    private double meanZ = 0;
    private double sinCalibrate = 0;
    private double cosCalibrate = 1;

    /**
     * a tag for logging
     */
    private static final String TAG = SensorDetectService.class.getSimpleName();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //This is to register the different sensors like acc, gyroscope and magnometer
    //It adds different listeners to them
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //SENSOR_DELAY_FASTEST (10ms), SENSOR_DELAY_GAME(20ms), SENSOR_DELAY_UI(65ms), SENSOR_DELAY_NORMAL(200ms)
        Log.d(TAG, "In OnStartCommand\n");
        if (mAccelerometer != null){
            mSensorManager.registerListener(this, mAccelerometer, 30000);//(SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, " Accelerometer Registered ..\n");
        }
        if (mGyroscope != null){
            mSensorManager.registerListener(this, mGyroscope, 30000);//SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "Gyroscope Registered ..\n");
        }
        if (mMagnetometer != null) {
            mSensorManager.registerListener(this, mMagnetometer, 30000);//SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "Magnetometer Registered ..\n");
        }
        return START_STICKY;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing
    }

    //This function filiters the data and uses the low pass filter
    public synchronized double[] IIRFilter(double[] signal, double[] a, double[] b) {

        double[] in = new double[b.length];
        double[] out = new double[a.length-1];

        double[] outData = new double[signal.length];

        for (int i = 0; i < signal.length; i++) {

            System.arraycopy(in, 0, in, 1, in.length - 1);
            in[0] = (double) signal[i];

            //calculate y based on a and b coefficients
            //and in and out.
            float y = 0;
            for(int j = 0 ; j < b.length ; j++){
                y += b[j] * in[j];

            }

            for(int j = 0;j < a.length-1;j++){
                y -= a[j+1] * out[j];
            }

            //shift the out array
            System.arraycopy(out, 0, out, 1, out.length - 1);
            out[0] = y;

            outData[i] = y;
        }
        return outData;
    }

    //This function runs when each sensor change, this function is changed
    //If change in accel, it will record accel, if its gyro change, record gyro data
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            double accx = event.values[0]; //- gravity[0];
            double accy = event.values[1]; //- gravity[1];
            double accz = -event.values[2]; //- gravity[2];


            //calibration-- calculate vect
            if(calibrateNum>0 && isCalibrate){
                calibrateNum--;
                meanX+=accz;
                meanZ+=accx;
            }else if(calibrateNum==0 && isCalibrate){
                meanX = meanX/40; //It collects 40 data to find the average and calculate sin and cos
                meanZ = meanZ/40;
                //No need for Y value

                isCalibrate=false;
                calibrateNum--;
                //OnSensor change is the loop
            }
            if(isCalibrateEnd && Math.abs(meanX)>0.05 && calibrateNum==-1 ){
                double temp = Math.sqrt(Math.pow(meanX,2)+Math.pow(meanZ,2));
                sinCalibrate = -meanX/temp;
                cosCalibrate = meanZ/temp;
                isCalibrateEnd=false;
                StartAutoCoachActivity.getMainActivity().sensorsCalibratedToast();
            }

            // do the calibration
            accz = accz*cosCalibrate + accx*sinCalibrate;


            //SensorContract.SensorEntry.COLUMN_ACC_Xthis one is the keyvalue is dictionary
            // SensorContract.SensorEntry.COLUMN_ACC_X is the key for value and accx is the value for valueIn DataBase->SensorContract you can see the declare
            value.put(SensorContract.SensorEntry.COLUMN_ACC_X, accx); //forward and backward motion
            value.put(SensorContract.SensorEntry.COLUMN_ACC_Y, accy);
            value.put(SensorContract.SensorEntry.COLUMN_ACC_Z, accz);


            if(value.get(SensorContract.SensorEntry.COLUMN_ACC_X)==null){
                System.out.println("Accelerometer Value is NULL");
            }
            acc_count = 1;


        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            value.put(SensorContract.SensorEntry.COLUMN_GYRO_X, (float) Math.toDegrees(event.values[0]));
            value.put(SensorContract.SensorEntry.COLUMN_GYRO_Y, (float) Math.toDegrees(event.values[1]));
            value.put(SensorContract.SensorEntry.COLUMN_GYRO_Z, (float) Math.toDegrees(event.values[2]));

            if(value.get(SensorContract.SensorEntry.COLUMN_GYRO_X)==null){
                System.out.println("Gyroscope Value is NULL");
            }
            gyo_count = 1;

        }

        if (acc_count == 1 && gyo_count==1) {
            // Log.d(TAG, "*** Count is: " + count +  " *** Pushing it to array list\n");
            acc_count = 0;
            gyo_count = 0;
            ///These will be stored in the database
            value.put(SensorContract.SensorEntry.COLUMN_DATE, System.currentTimeMillis());
            value.put(SensorContract.SensorEntry.COLUMN_SPEED, StartAutoCoachActivity.getMainActivity().getSpeed());
            value.put(SensorContract.SensorEntry.COLUMN_GPS_LAT, 0);
            value.put(SensorContract.SensorEntry.COLUMN_GPS_LONG, 0);
            value.put(SensorContract.SensorEntry.COLUMN_CLASSIFICATION, 0); //Event type

            //Store the trip ID in value
            value.put(SensorContract.SensorEntry.COLUMN_TRIP_ID, trip_id);

            //catch event --> because it is landscape view
            double acc_x = (double)value.get(SensorContract.SensorEntry.COLUMN_ACC_Z);
            double acc_y = (double)value.get(SensorContract.SensorEntry.COLUMN_ACC_Y);
            double acc_z = (double)value.get(SensorContract.SensorEntry.COLUMN_ACC_X);
            long timestamp = (long)value.get(SensorContract.SensorEntry.COLUMN_DATE);
            double gyro_x = (float)value.get(SensorContract.SensorEntry.COLUMN_GYRO_X);
            double gyro_y = (float)value.get(SensorContract.SensorEntry.COLUMN_GYRO_Y);
            double gyro_z = (float)value.get(SensorContract.SensorEntry.COLUMN_GYRO_Z);
            double speed = (int) value.get(SensorContract.SensorEntry.COLUMN_SPEED);

            dataQueue.add(new double[]{timestamp, speed, acc_x, acc_y, acc_z, gyro_x, gyro_y, gyro_z});

            //collect 120 data to filter
            //120 is filtered, because the low pass need more data to get good effect
            if (dataQueue.size()==120){

                //when the queue for standard deviation calculation is full. 40 is used to calculate, the other 14 is padding for filter
                Event eventX = detectXEvent(dataQueue.toArray());
                Event eventY = detectYEvent(dataQueue.toArray());

                if(eventX!=null){
                    //System.out.println("get event from x axis");
                    Bundle bundle = new Bundle();
                    com.example.autocoach20.Activities.EventsCache.EventsCache cache = new com.example.autocoach20.Activities.EventsCache.EventsCache(getApplicationContext());
                    bundle.putString("event", cache.saveEvent(eventX));
                    broadcast_intent.putExtras(bundle);
                    sendBroadcast(broadcast_intent);
                }

                if(eventY!=null){
                    //System.out.println("get event from y axis");
                    Bundle bundle = new Bundle();
                    com.example.autocoach20.Activities.EventsCache.EventsCache cache = new com.example.autocoach20.Activities.EventsCache.EventsCache(getApplicationContext());
                    bundle.putString("event", cache.saveEvent(eventY));
                    //Disabled old code
                    //bundle.putSerializable("event", eventY);
                    broadcast_intent.putExtras(bundle);
                    sendBroadcast(broadcast_intent);
                }
            }

            ContentValues copy_cv = new ContentValues(value);
            values.add(copy_cv);

            if (copy_cv == null) Log.e(TAG, "CONTENT VALUE IS NULL- while pushing into arrayList ");

            value.clear();

            /* TO_DO: COMMENT THESE OUT */
            //mSensorManager.unregisterListener(this);
            //stopSelf();
        }

        // if collect enough data store it into database
        //We can change the 200 to some other value
        //We can use 500, the thread will need more time
        //500 takes longer time to store
        if (values.size() > 200) {
            // Log.d(TAG, "~~~~~~ Starting Bulk Sync ~~~~ \n");
            //create a copy of array list and insert
            final ArrayList<ContentValues> copy_list = new ArrayList<>(values);
            final Context context = this;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dataLock.lock();
                    bulk_insert(context, copy_list);
                    dataLock.unlock();
                }
            }).start();
            values.clear();
        }
    }

    public double getStandardDeviation(double[] x) {
        int m = x.length;
        double sum = 0;
        for (double v : x) {// 求和 //sum of all data in the List
            sum += v;
        }
        double dAve = sum / m;// 求平均值 //get the average of sum/total numbers
        double dVar = 0;
        for (double v : x) {// 求方差 //get the deviation
            dVar += (v - dAve) * (v - dAve);
        }
        return Math.sqrt(dVar / m);
    }


    //This function filters the 120 data points and then calculate the STD
    //Requires Java API higher tha 1.7
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Event detectXEvent(Object[] dataQueue){
        double[] data = new double[dataQueue.length];
        double[] currentData = (double[]) dataQueue[105];

        for(int i = 0; i<dataQueue.length;i++){
            double[] temp = (double[]) dataQueue[i];
            data[i] = temp[2];
        }
        double[] filteredData = IIRFilter(data, iirFilterCoefficients.a, iirFilterCoefficients.b);
        //calculate standard deviation
        double[] dataForStd = new double[40];
        for(int i = 0; i<dataQueue.length; i++){
            if(i>65 && i<106){
                dataForStd[i-66] = filteredData[i]; // get the acc x value from dataQueue
            }

        }
        double currentStd = getStandardDeviation(dataForStd);
        stdXQueue.add(currentStd);
        double accxfiltered = filteredData[105];
        //Log.i("acc filtered: ",""+accxfiltered);
//        System.out.println(accxfiltered);
        boolean isAccLog = false;
        boolean isBrakeLog = false;


        //This is to catch the begining of the event
        if (accxfiltered > 1.2 && currentStd>0.15 && accEventDataNum==0){
            accEventDataNum++;
            Object[] stdArr = stdXQueue.toArray();
            int index = IntStream.range(0, stdArr.length).reduce((i, j) -> (double)stdArr[i] > (double)stdArr[j] ? j : i).getAsInt();
            double[] recordData = (double[]) dataQueue[66+index]; //defined data Queue
            accEvent = new Event((long) recordData[0],0); //recordData[0] = timestamp

            //Since 120 dat is filtered
            //66 is a padding and we use only the data after the padding up to 120 (67-120) used for STD to calculate the beging and end
            for(int i = index+66; i<=105; i++){
                accEvent.add_Value((double[]) dataQueue[i]);
            }

            //Event is not finished
        }else if(accxfiltered>0.5 && accEventDataNum>0){
            accEventDataNum++;
            accFault = 5;
            isAccLog = true;

            //Judge if the event is suddenly getting below the number 0.5 ..
            //signal is fluctuating ..
        }else if (accxfiltered<=0.5 && accFault>0 && accEventDataNum>0){
            accEventDataNum++;
            accFault--; //give it a fault, if fluctuation is larger than some number, event is finished
            //If its smaller then event is continueing
            isAccLog = true; //Need to log data in the event

            //Finished event
        }else if((accxfiltered<=0.5 || currentStd<0.1 )&& accEventDataNum>0){
            accFault = 5; //5 is a small number and most of the fluctuation is not higher than 5
            if (accEventDataNum> stdWin){ //stdWin = 33 and accEventNum is the number of data points for hte event
                //This is greater than 1 second
                accEvent.setEnd((long) currentData[0]); //setEnd Time
                accEvent.add_Value(currentData);
                accEventDataNum = 0;

                //Refer for Event for details of object
                return accEvent; //This returns an object of accelerometer event
            }else{
                accEventDataNum = 0;
            }
        }

        if (accxfiltered <-1.5 && currentStd>0.15 && brakeEventDataNum==0){
            brakeEventDataNum++;
            Object[] stdArr = stdXQueue.toArray();
            int index = IntStream.range(0, stdArr.length).reduce((i, j) -> (double)stdArr[i] > (double)stdArr[j] ? j : i).getAsInt();
            double[] recordData = (double[]) dataQueue[66+index];
            brakeEvent = new Event((long) recordData[0],1);
            for(int i = index+66;i<=105;i++){
                brakeEvent.add_Value((double[]) dataQueue[i]);
            }
        }else if(accxfiltered<-0.5 && brakeEventDataNum>0){
            brakeEventDataNum++;
            brakeFault = 5;
            isBrakeLog = true;
        }else if (accxfiltered>=-0.5 && brakeFault>0 && brakeEventDataNum>0){
            brakeEventDataNum++;
            brakeFault--;
            isBrakeLog = true;
        }else if((accxfiltered>=-0.5 || currentStd<0.1 )&& brakeEventDataNum>0){
            brakeFault = 5;
            if (brakeEventDataNum> stdWin){
                brakeEvent.setEnd((long) currentData[0]);
                brakeEvent.add_Value(currentData);
                brakeEventDataNum = 0;
                return brakeEvent;
            }else{
                brakeEventDataNum = 0;
            }
        }

        if(isAccLog){
            accEvent.add_Value(currentData); //whole array of data -- one data point
        }
        if(isBrakeLog){
            brakeEvent.add_Value(currentData);
        }

        return null;
    }


    //Swerve is determined in the turn too!
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Event detectYEvent(Object[] dataQueue){
        double[] data = new double[dataQueue.length];
        double[] currentData = (double[]) dataQueue[105];

        for(int i = 0; i<dataQueue.length;i++){
            double[] temp = (double[]) dataQueue[i];
            data[i] = temp[3];
        }
        double[] datafiltered  = IIRFilter(data, iirFilterCoefficients.a, iirFilterCoefficients.b);
        //calculate standard deviation
        double[] dataForStd = new double[40];
        for(int i = 0; i<dataQueue.length; i++){
            if(i>65 && i<106){
                dataForStd[i-66] = datafiltered[i]; // get the acc x value from dataQueue
            }

        }
        double currentStd = getStandardDeviation(dataForStd);
        stdYQueue.add(currentStd);
        double accyfiltered = datafiltered[105];
//        System.out.println(accyfiltered);
        boolean isleftLog = false;
        boolean isrightLog = false;


        if (accyfiltered > 1.5 && currentStd>0.15 && lturnEventDataNum==0){
            lturnEventDataNum++;
            Object[] stdArr = stdXQueue.toArray();
            int index = IntStream.range(0, stdArr.length).reduce((i, j) -> (double)stdArr[i] > (double)stdArr[j] ? j : i).getAsInt();
            double[] recordData = (double[]) dataQueue[66+index];
            lturnEvent = new Event((long) recordData[0],2);
            for(int i = index+66;i<=105;i++){
                lturnEvent.add_Value((double[]) dataQueue[i]);
            }

        }else if(accyfiltered>0.5 && lturnEventDataNum>0){
            lturnEventDataNum++;
            lturnFault = 5;
            isleftLog = true;
        }else if (accyfiltered<=0.5 && lturnFault>0 && lturnEventDataNum>0){
            lturnEventDataNum++;
            lturnFault--;
            isleftLog = true;
        }else if((accyfiltered<=0.5 || currentStd<0.1 )&& lturnEventDataNum>0){
            lturnFault = 5;
            if (lturnEventDataNum> stdWin){
                lturnEvent.setEnd((long) currentData[0]);
                lturnEvent.add_Value(currentData);
                lturnEventDataNum = 0;
                return lturnEvent;
            }else{
                lturnEventDataNum = 0;
            }
        }


        if (accyfiltered < -1.5 && currentStd>0.15 && rturnEventDataNum==0){
            rturnEventDataNum++;
            Object[] stdArr = stdXQueue.toArray();
            int index = IntStream.range(0, stdArr.length).reduce((i, j) -> (double)stdArr[i] > (double)stdArr[j] ? j : i).getAsInt();
            double[] recordData = (double[]) dataQueue[66+index];
            rturnEvent = new Event((long) recordData[0],2);
            for(int i = index+66;i<=105;i++){
                rturnEvent.add_Value((double[]) dataQueue[i]);
            }

        }else if(accyfiltered<-0.5 && rturnEventDataNum>0){
            rturnEventDataNum++;
            rturnFault = 5;
            isrightLog = true;
        }else if (accyfiltered>=-0.5 && rturnFault>0 && rturnEventDataNum>0){
            rturnEventDataNum++;
            rturnFault--;
            isrightLog = true;
        }else if((accyfiltered>=-0.5 || currentStd<0.1 )&& rturnEventDataNum>0){
            rturnFault = 5;
            if (rturnEventDataNum> stdWin){
                rturnEvent.setEnd((long) currentData[0]);
                rturnEvent.add_Value(currentData);
                rturnEventDataNum = 0;
                return rturnEvent;
            }else{
                rturnEventDataNum = 0;
            }
        }

        if(isleftLog){
            lturnEvent.add_Value(currentData);
        }
        if(isrightLog){
            rturnEvent.add_Value(currentData);
        }

        return null;
    }


    public void bulk_insert (Context context, ArrayList<ContentValues> values) {
        SensorDataSave.syncSensor(context, values);
    }

    public void onDestroy () {
        //This is called when stopService() is called. Not called when app is terminated by user.
        //Call stopService() in MainActivity to catch app terminations by user and take appropriate actions
        //TODO: Check if you want to sync pending items in arraylist to the DB
        Log.w(TAG, "App terminated by user!! ");
    }
}
