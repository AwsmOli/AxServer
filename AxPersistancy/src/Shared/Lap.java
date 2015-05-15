package Shared;

/**
 * Represents a Lap
 * Created by Awli on 29.01.2015.
 */
public class Lap  {
    private long time;
    private Course Course;

    public Lap(long time, Course Course) {
        this.time = time;
        this.Course = Course;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Course getCourse() {
        return Course;
    }

    public void setCourse(Course Course) {
        this.Course = Course;
    }
}
