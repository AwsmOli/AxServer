package eu.isawsm.accelerate.server;

import Shared.Lap;
import com.google.gson.Gson;

import java.util.Date;

/**
 * Created by ofade on 15.08.2015.
 */
public class Passing {
    private long time;
    private Date timeStamp;
    private long transponderID;
    private Type Type;

    public enum Type{
        MyLaps,
        Robitronic
    }


    public Passing(long time, long transponderID, Type type) {
        this.time = time;
        this.timeStamp = new Date();
        this.transponderID = transponderID;
        Type = type;
    }

    public long getTimne() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTransponderID() {
        return transponderID;
    }

    public void setTransponderID(long transponderID) {
        this.transponderID = transponderID;
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type type) {
        Type = type;
    }
}
