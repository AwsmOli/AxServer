package Shared;

import java.util.Date;

/**
 * Represents a Lap
 * Created by Awli on 29.01.2015.
 */
public class Lap  {
    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    private Course Course;
    public long startTime;
    public long endTime;


    public Lap(long startTime, Course Course) {
        this.startTime = startTime;
        this.Course = Course;
    }

    public long getTime() {
        if(endTime == 0) return new Date().getTime() - startTime;
        return endTime - startTime;
    }

    public Course getCourse() {
        return Course;
    }

    public void setCourse(Course Course) {
        this.Course = Course;
    }


}
