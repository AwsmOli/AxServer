package eu.isawsm.accelerate.server;

import java.util.Date;

/**
 * Created by ofade on 15.08.2015.
 */
public class Passing {
    private long time;
    private Date timeStamp;
    private long transponderID;
    private DecoderType type;

    public enum DecoderType {
        MyLaps,
        Robitronic
    }


    public Passing(long time, long transponderID, DecoderType type) {
        this.time = time;
        this.timeStamp = new Date();
        this.transponderID = transponderID;
        this.type = type;
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

    public DecoderType getType() {
        return type;
    }

    public void setType(DecoderType type) {
        this.type = type;
    }
}
