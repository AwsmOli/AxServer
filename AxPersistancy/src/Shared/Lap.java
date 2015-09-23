package Shared;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a Lap
 * Created by Awli on 29.01.2015.
 */
public class Lap implements Serializable, Comparable<Lap> {
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
        return endTime - startTime;
    }

    public Course getCourse() {
        return Course;
    }

    public void setCourse(Course Course) {
        this.Course = Course;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lap lap = (Lap) o;

        if (startTime != lap.startTime) return false;
        if (endTime != lap.endTime) return false;
        return !(Course != null ? !Course.equals(lap.Course) : lap.Course != null);

    }

    @Override
    public int hashCode() {
        int result = Course != null ? Course.hashCode() : 0;
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + (int) (endTime ^ (endTime >>> 32));
        return result;
    }

    @Override
    public int compareTo(Lap o) {

        if (o.startTime < startTime)
            return -1;

        if (o.startTime > startTime)
            return 1;

        return 1;
    }
}
