package Shared;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a RC-Car
 * Created by Awli on 29.01.2015.
 */
public class Car implements Serializable {

    private static final int MINLAPS = 10;
    private Model Model;
    private Clazz Clazz;
    private long transponderID;
    private Bitmap picture;
    private ConcurrentSkipListSet<Lap> Laps;

    public Car(Model Model, Clazz Clazz, long transponderID, Bitmap picture) {
        this.Model =  Model;
        this.Clazz =  Clazz;
        this.transponderID = transponderID;
        this.picture = picture;
        Laps = new ConcurrentSkipListSet<>();
    }

    private static double median(double[] m) {
        Arrays.sort(m);
        int middle = m.length / 2;
        if (m.length % 2 == 1) {
            return m[middle];
        } else {
            return (m[middle - 1] + m[middle]) / 2.0;
        }
    }

    public Model getModel() {
        return Model;
    }

    public void setModel(Model Model) {
        this.Model = Model;
    }

    public Clazz getClazz() {
        return Clazz;
    }

    public void setClazz(Clazz Clazz) {
        this.Clazz = (Clazz) Clazz;
    }

    public long getTransponderID() {
        return transponderID;
    }

    public void setTransponderID(long transponderID) {
        this.transponderID = transponderID;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public double getAvgTime(Course course) {
        return getAvgTime(course, 0);
    }

    public double getAvgTime(Course course, int count) {
        List<Lap> Laps = count==0 ? getLapsCopy(): getLapsCopy(200);

        if (Laps.isEmpty()) return 0.0;

        long sum = 0;
        for (Lap l : Laps) {
            if(l.getCourse().equals(course))
                sum += l.getTime();
        }
        return sum / Laps.size();
    }

    public double getBestTime(Course course) {

        List<Lap> Laps = getLapsCopy();



        if (Laps.isEmpty()) return 0;
        long bestTime = ((Lap)Laps.toArray()[0]).getTime();

        for (Lap l : Laps) {
            if(l.getCourse().equals(course))
                if (l.getTime() < bestTime) {

                    bestTime = l.getTime();
                }
        }
        return bestTime;
    }

    public int getLapCount(Course course) {
        List<Lap> Laps = getLapsCopy();

        int retVal = 0;

        for(Lap l: Laps){
            if(l.getCourse().equals(course))
                retVal++;
        }
        return retVal;
    }

    public double getConsistency(Course course) {
        return getConsistency(course,0);
    }

    public double getConsistency(Course course, int count) {

        if (getLapCount(course) < MINLAPS) return -1;

        List<Lap> Laps = count==0 ? getLapsCopy(): getLapsCopy(200);

        double[] times = new double[Laps.size()];
        int i = 0;
        for (Lap l : Laps) {
            if(l.getCourse().equals(course)){
                times[i] = l.getTime();
                i++;
            }
        }
        //Median for all Times
        double median1 = median(times);

        //Each times Variance to Median 1
        double[] variance = new double[times.length];

        for (i = 0; i < times.length; i++) {
            variance[i] = Math.abs(times[i] - median1);
        }

        //Median of Variance
        double median2 = median(variance);

        //Variance in Percent
        double varPercent = 1 - (median2 / median1);


        double consistency = (((varPercent - 0.4) / 60) * 100) * 100;

        BigDecimal bd = new BigDecimal(consistency).setScale(2, RoundingMode.HALF_EVEN);

        return bd.doubleValue();
    }

    public int getRank() {
        return 0;
    }

    public ConcurrentSkipListSet<Lap> getLaps() {
        return Laps;
    }

    public void addLap(Lap Lap) {
        Laps.add(Lap);
    }

    public String getName() {
        String retVal = "";
        if (getModel() != null && getModel().getManufacturer() != null) {
            retVal += getModel().getManufacturer().getName();
            retVal += " "+getModel().getName();
        } else {
            return getTransponderID() + "";
        }

        return retVal.trim();
    }

    private List<Lap> getLapsCopy (){
        return new ArrayList<>(Laps);
    }

    private List<Lap> getLapsCopy(int count){

            if(Laps.size() > count){
                return new ArrayList<>(Laps).subList(Laps.size()-count-1,Laps.size()-1);
            }
            return getLapsCopy();

    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Car car = (Car) o;

        return transponderID == car.transponderID;

    }

    public int hashCode() {
        return (int) (transponderID ^ (transponderID >>> 32));
    }

    public Lap currentLap;

    public Lap getCurrentLap() {
        return currentLap;
    }

    public void setCurrentLap(Lap currentLap) {
        this.currentLap = currentLap;
    }

    public Lap finishCurrentLap(long time) {
        Lap finishedLap = getCurrentLap();

        finishedLap.setEndTime(time);
        if (finishedLap.getTime() >= finishedLap.getCourse().getMaxTime() || finishedLap.getTime() <= finishedLap.getCourse().getMinTime()) {
            return null;
        }

        addLap(finishedLap);
        setCurrentLap(new Lap(time, getCurrentLap().getCourse()));
        return finishedLap;
    }

    public Car merge(Car c2) {
        if(c2.getModel() != null){
            setModel(c2.getModel());
        }
        if(c2.getClazz() != null){
            setClazz(c2.getClazz());
        }
        if(c2.getName() != null){
            setClazz(c2.getClazz());
        }
       getLaps().addAll(c2.getLaps());

        return this;
    }
}
