package Shared;

import java.io.Serializable;

/**
 * Represents a Track
 * Created by Awli on 29.01.2015.
 */
public class Track implements Serializable {
    private String type;
    private Course course;

    public Track(String type, Course course) {
        this.type = type;
        this.course = course;
    }
    public Track(){

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
