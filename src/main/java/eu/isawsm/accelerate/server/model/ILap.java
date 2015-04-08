package eu.isawsm.accelerate.server.model;

/**
 * Created by ofade_000 on 04.04.2015.
 */
public interface ILap {
    long getTime();

    void setTime(long time);

    ICourse getCourse();

    void setCourse(ICourse ICourse);
}
