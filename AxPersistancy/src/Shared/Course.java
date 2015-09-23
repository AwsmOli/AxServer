package Shared;

import java.io.Serializable;

/**
 * Represents a Course
 * Created by Awli on 31.01.2015.
 */
public class Course implements Serializable {
    private String condition;
    private String name;
    private boolean inReverse;
    private long minTime;
    private long maxTime;

    public Course(String condition, String name, boolean inReverse, long minTime, long maxTime) {
        this.condition = condition;
        this.name = name;
        this.inReverse = inReverse;
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

     
    public String getCondition() {
        return condition;
    }

     
    public void setCondition(String condition) {
        this.condition = condition;
    }

     
    public String getName() {
        return name;
    }

     
    public void setName(String name) {
        this.name = name;
    }

     
    public boolean isInReverse() {
        return inReverse;
    }

     
    public void setInReverse(boolean inReverse) {
        this.inReverse = inReverse;
    }

     
    public long getMinTime() {
        return minTime;
    }

     
    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }

     
    public long getMaxTime() {
        return maxTime;
    }

     
    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;

        Course course = (Course) o;

        if (name != null ? !name.equals(course.name) : course.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
