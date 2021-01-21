package com.example.autocoach20.Activities.SyncServices.Bean;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.IntStream;

import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilterCoefficients;
import biz.source_code.dsp.filter.IirFilterDesignExstrom;
//lib > dsp-collection.jar

//There are 16 features

//Serialazable sends a binary
public class Event implements Serializable {

    // *************************************************************** //
    // FIELDS
    // *************************************************************** //

    private static final long serialVersionUID = 123456L; //For converting to serializable, we need a UID

    public Queue<double[]> rawData;                        //All event Raw Data (one event)
    public Queue<double[]> filteredData;                   //Newly added Filtered Data
    private long start;                                     //Event start timestamp
    private long end;                                       //Event end timestamp
    private int type;                                       //type of event (0, 1, 2), where 0 is Acceleration, 1 is brake, 2 is turn and swerve

    private Double[] eventTimestampColumn = null;           //holds a whole column of event timestamp
    private Double[] eventSpeedColumn = null;               //holds a whole column of event speed
    private Double[] eventAccelerometerXColumn = null;      //holds a whole column of event acceleration x
    private Double[] eventAccelerometerYColumn = null;      //holds a whole column of event acceleration y
    private Double[] eventAccelerometerZColumn= null;       //holds a whole column of event acceleration z
    private Double[] eventGyroscopeXColumn = null;          //holds a whole column of event gyroscope x
    private Double[] eventGyroscopeYColumn = null;          //holds a whole column of event gyroscope y

    //Transient: since this file is being put in a serialiazable format.
    //However, since this is a function that is from outside, we don't need it so we skip it by using transient
    private transient IirFilterCoefficients iirFilterCoefficients = IirFilterDesignExstrom.design(FilterPassType.lowpass, 5,
            10.0/340, 10.0 / 170); //These values (i: iterations, v:frequency, v1)

    // *************************************************************** //
    // PATTERN LETTERS FIELDS
    // *************************************************************** //

    private int classification;     //Event Classification (safe, medium-risk or high-risk) (brake, acceleration, turn, or swerve)
    private String letter;
    private String[] patternAlphabet = {
            "a",        //Event Type: Safe Brake
            "b",        //Event Type: Medium-Risk Brake
            "c",        //Event Type: High-Risk Brake
            "h",        //Event Type: Safe Acceleration
            "i",        //Event Type: Medium-Risk Acceleration
            "j",        //Event Type: High-Risk Acceleration
            "o",        //Event Type: Safe Turn
            "p",        //Event TYpe: Medium-Risk Turn
            "q",        //Event Type: High-Risk Turn
            "v",        //Event Type: Safe Swerve (Lane-change)
            "w",        //Event Type: Medium-Risk Swerve
            "x"};       //Event Type: High-Risk Swerve


    // *************************************************************** //
    // SETTERS AND GETTERS
    // *************************************************************** //

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public Queue<double[]> getRawData() {
        return rawData;
    }

    public void setRawData(Queue<double[]> rawData) {
        this.rawData = rawData;
    }

    public void setStartTime(){
        Queue<double[]> queue = this.rawData;
        double[] head = queue.peek();
        long startTime = (long) head[0];

        this.start = startTime;
        Log.w("StartTime", "setStartTime "+this.start );
    }

    // *************************************************************** //
    // CONSTRUCTORS
    // *************************************************************** //
    public Event( long start, int type){
        this.start = start;                 //Start time of the event
        this.type = type;                   //Type of the event (Either 0:brake, 1:acceleration, or 2:turn or swerve)
        this.end = 0;                       //End time of the event
        this.rawData = new LinkedList<>();  //All raw data of that specific event
        this.filteredData = new LinkedList<>(); //All filtered data of that specific event
        this.classification = -1;           //Event Type and Risk level (0~12 types of events)
        this.letter = "";                   //Letter representing type of event
    }

    private void getByColumn(double[][] rawData){
        int rowlength = rawData.length;

        eventTimestampColumn = new Double[rowlength];       //holds a whole column of event timestamp
        eventSpeedColumn = new Double[rowlength];           //holds a whole column of event speed
        eventAccelerometerXColumn = new Double[rowlength];  //holds a whole column of event acceleration x
        eventAccelerometerYColumn = new Double[rowlength];  //holds a whole column of event acceleration y
        eventAccelerometerZColumn = new Double[rowlength];  //holds a whole column of event acceleration z
        eventGyroscopeXColumn = new Double[rowlength];      //holds a whole column of event gyroscope x
        eventGyroscopeYColumn = new Double[rowlength];      //holds a whole column of event gyroscope


        for(int i=0;i < rowlength;i++){
            eventTimestampColumn[i] = rawData[i][0];
            eventSpeedColumn[i] = rawData[i][1];
            eventAccelerometerXColumn[i] = rawData[i][2];
            eventAccelerometerYColumn[i] = rawData[i][3];
            eventAccelerometerZColumn[i] = rawData[i][4];
            eventGyroscopeXColumn[i] = rawData[i][5];
            eventGyroscopeYColumn[i] = rawData[i][6];
        }
        //return templist;
    }


    /**
     * std dev σ=sqrt(s^2)
     * @param x
     * @return
     */
    private double getStandardDeviation(Double[] x) {
        int m = x.length;
        double sum = 0;
        for (int i = 0; i < m; i++) {// get sum
            sum += x[i];
        }
        double dAve = sum / m;// get mean
        double dVar = 0;
        for (int i = 0; i < m; i++) {// get variance
            dVar += (x[i] - dAve) * (x[i] - dAve);
        }
        return Math.sqrt(dVar / m);
    }

    /**
     * mean
     * @param x
     * @return
     */
    private double getAverage(Double[] x) {
        double sum = 0.0;
        double mean = 0.0;

        for (int i = 0; i < x.length; i++) {
            sum += x[i];
        }
        mean = sum/x.length;
        return mean;
    }

    /**
     * abs
     * @param x
     * @return
     */
    private Double[] getAbs(Double[] x) {
        Double[] y = new Double[x.length];
        for (int i = 0; i < x.length; i++) {
            if(x[i] < 0) {
                x[i] = -x[i];
            }
            y[i] = x[i];
        }
        return y;
    }

    //return col data
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Double> getArray() {
        double[][] array = rawData.toArray(new double[0][0]);
        getByColumn(array);//get col data

        iirFilterCoefficients = IirFilterDesignExstrom.design(FilterPassType.lowpass, 5,
                10.0/340, 10.0 / 170);

        eventAccelerometerXColumn = IIRFilter(eventAccelerometerXColumn, iirFilterCoefficients.a, iirFilterCoefficients.b);
        eventAccelerometerYColumn = IIRFilter(eventAccelerometerYColumn, iirFilterCoefficients.a, iirFilterCoefficients.b);

//        System.out.println("acc avlue:");
//        for(double acc:list2){
//            System.out.print(acc);
//            System.out.print("  ");
//        }

//        def calculate_feature(self,vect):
//        maxAX = max(vect[:, 3]) maximum value
        //find largest value of accel. x
        int index1 = IntStream.range(0, eventAccelerometerXColumn.length).reduce((i, j) -> eventAccelerometerXColumn[i] > eventAccelerometerXColumn[j] ? i : j).getAsInt();
        double maxAX = eventAccelerometerXColumn[index1];//Math.max(list3);
//        maxAY = max(vect[:, 2])

        //largest value of accel. y
        int index2 = IntStream.range(0, eventAccelerometerYColumn.length).reduce((i, j) -> eventAccelerometerYColumn[i] > eventAccelerometerYColumn[j] ? i : j).getAsInt();
        double maxAY = eventAccelerometerYColumn[index2]; //max((list2));

//        minAX = min(vect[:, 3]) minimum value
        //miniumum of accel x.
        int index3 = IntStream.range(0, eventAccelerometerXColumn.length).reduce((i, j) -> eventAccelerometerXColumn[i] > eventAccelerometerXColumn[j] ? j : i).getAsInt();
        double minAX = eventAccelerometerXColumn[index3];//Math.min(list3);

//        minAY = min(vect[:, 2])
        //minumum of accel y
        int index4 = IntStream.range(0, eventAccelerometerYColumn.length).reduce((i, j) -> eventAccelerometerYColumn[i] > eventAccelerometerYColumn[j] ? j : i).getAsInt();
        double minAY = eventAccelerometerYColumn[index4]; //max((list2));

        //rangeAX is the Acceleration X range of max accel - min accel.
//        rangeAX = maxAX - minAX
        double rangeAX = maxAX - minAX;

        //rangeAX is the Acceleration Y range of max accel - min accel.
//        rangeAY = maxAY - minAY
        double rangeAY = maxAY - minAY;

        //stdAX is the std in X acceleration
//        stdAX = np.std(vect[:, 3]) std dev
        double stdAX = getStandardDeviation(eventAccelerometerXColumn);

        //stdAX is the std in Y acceleration
//        stdAY = np.std(vect[:, 2])
        double stdAY = getStandardDeviation(eventAccelerometerYColumn);


//        meanAX = np.mean(vect[:, 3]) mean
        double meanAX = getAverage(eventAccelerometerXColumn);
//        meanAY = np.mean(vect[:, 2])
        double meanAY = getAverage(eventAccelerometerYColumn);
//        meanOX = np.mean(vect[:, 5])
        double meanOX = getAverage(eventGyroscopeXColumn);

        //Mean of Speed
//        meanSP = np.mean(vect[:, 1])
        double meanSP = getAverage(eventSpeedColumn);

        //duration of the event ~ end - start
//        t = (vect[-1, 1] - vect[0, 1]) / 1000
        double t = (array[array.length - 1][0] - array[0][0])/1000;

        //Speed difference ~ end speed - start speed
//        differenceSP = vect[-1, 1] - vect[0, 1]
        double differenceSP = array[array.length - 1][1] - array[0][1];

        //AccelerationX Start - AccelerationX End
//        StartEndAccx = vect[0, 3] + vect[-1, 3]
        double StartEndAccx = array[0][2] - array[array.length - 1][2];

        //AccelerationY Start - AccelerationY End
//        StartEndAccy = vect[0, 2] + vect[-1, 2]
        double StartEndAccy = array[0][3] - array[array.length - 1][3];


//        axis = vect[0, -1]
        double axis = 0;
        if(this.type == 0 || this.type == 1){ //Acceleration=0, Brake=1
            axis = 0; //X-Axis
        }else{  //
            axis = 1; //Y-Axis
        }

//        maxOX = max(abs(vect[:, 5]))
        Double[] list51 = getAbs(eventGyroscopeXColumn);

        //Gets the maxiumum value of GyroX
        int index5 = IntStream.range(0, list51.length).reduce((i, j) -> list51[i] > list51[j] ? i : j).getAsInt();

        double maxOX = list51[index5];
//        maxOY = max(abs(vect[:, 6]))
//        Double[] list61 = getAbs(list6);
//        int index6 = IntStream.range(0, list61.length).reduce((i, j) -> list61[i] > list61[j] ? i : j).getAsInt();
//        double maxOY = list61[index6];
//        maxOri = max(maxOX, maxOY)
        double maxOri = maxOX; //Math.max(maxOX,maxOY);


//        maxAccX = max(abs(vect[:, 3])) abs
//        Double[] list31 = getAbs(list3);
//        int index7 = IntStream.range(0, list31.length).reduce((i, j) -> list31[i] > list31[j] ? i : j).getAsInt();
//        double maxAccX = list51[index5];
//        maxAccY = max(abs(vect[:, 2]))
        Double[] list31 = getAbs(eventAccelerometerYColumn);

        //Gets the maximum Acceleration Y in absolute value
        int index8 = IntStream.range(0, list31.length).reduce((i, j) -> list31[i] > list31[j] ? i : j).getAsInt();
        double maxAccY = list31[index8];

        //Returns features in ArrayList a
        ArrayList<Double> a = new ArrayList<Double>(Arrays.asList(rangeAX, rangeAY, stdAX, stdAY, meanAX, meanAY, meanOX, maxOri, maxAX, minAX, maxAccY,
                meanSP, StartEndAccx, StartEndAccy, t, axis));  //# 99% 86%rawData
        return a;
    }

    //Normalize
    public ArrayList<Double> normalize(ArrayList<Double> features){
        ArrayList<Double> newFeature = new ArrayList<>();
        //These are the 16 SVM features min and max values
        //This section should be changed when the model is changed
        double[] max = {6.3804, 5.55909, 2.1217, 1.9062, 2.77235, 2.6795, 21.5806, 78.3708, 4.79105, 1.22073, 5.2711, 85.9737, 5.3705, 2.4869, 35.337, 1.0};
        double[] min = {0.7251, 0.0867, 0.0491, 0.0277, -4.21123, -2.8765, -23.2157, 3.0795, -0.6511, -6.3957, 0.0968,  0,  -6.9642, -4.9924, 0.835, 0.0};
        int index = 0;

        for(double feature: features){
            newFeature.add((feature-min[index])/(max[index]-min[index]));
            index++;
        }
        return newFeature;
    }


    //When logging the data into the class
    //When a data point is being recorded, it will call this function
    //For example, each one of the 33 readings happening within one second will call this function
    public void add_Value(double[] data){
        this.rawData.offer(data);
    }


    public double[][] get_raw_matrix(){
        return (double[][]) this.rawData.toArray(); //Zehua's line, not working?
    }

    /**
     * @author Zahraa Marafie
     * @return
     */
    public long getStartTime(){
        //Object[] rawDataObject = this.rawData.toArray();
        //double[] rawDataConverted;

        //rawDataConverted = (double[]) rawDataObject[0]; //Data of Event timestamps

        //return rawDataConverted[0];
        return this.start;
    }



    public void setType(int type){
        this.type = type;
    }

    //Get type of the event
    public int getType(){
        return this.type;
    }

    //Set end time of the event (timestamp)
    public void setEnd(long end){
        this.end = end;
    }

    public long getEnd(){
        return this.end;
    }

    //Get Duration of the event
    public double getDuration(){
        return ((double)this.end-(double)this.start)/1000;
    }

    //Set the risk-level of the event
    public void setClassification(int level){
        this.classification = level;
    }

    public int getClassification(){
        return this.classification;
    }

    //THIS DOES NOT WORK! DO NOT CALL IT OR FIX IT TO USE IT:)
    //Alphabitical letter of events ~ e.g. a: safe brake
    public String getLetter(){
        return this.patternAlphabet[this.classification];
    }

    //Using low pass filter
    private synchronized Double[] IIRFilter(Double[] signal, double[] a, double[] b) {

        double[] in = new double[b.length];
        double[] out = new double[a.length-1];

        Double[] outData = new Double[signal.length];

        for (int i = 0; i < signal.length; i++) {

            System.arraycopy(in, 0, in, 1, in.length - 1);
            in[0] = (double) signal[i];

            //calculate y based on a and b coefficients
            //and in and out.
            Double y = (double) 0;
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

}
