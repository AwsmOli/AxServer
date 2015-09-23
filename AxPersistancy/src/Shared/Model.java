package Shared;

import java.io.Serializable;

/**
 * Represents a Model
 * Created by Awli on 31.01.2015.
 */
public class Model implements Serializable {

    private Manufacturer Manufacturer;
    private String name;
    private String drivetrain;
    private String motor;
    private String type;
    private String scale;

    public Model(Manufacturer Manufacturer, String name, String drivetrain, String motor, String type, String scale) {
        this.Manufacturer = Manufacturer;
        this.name = name;
        this.drivetrain = drivetrain;
        this.motor = motor;
        this.type = type;
        this.scale = scale;
    }

    public Manufacturer getManufacturer() {
        return Manufacturer;
    }

    
    public void setManufacturer(Manufacturer Manufacturer) {
        this.Manufacturer = Manufacturer;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public String getDrivetrain() {
        return drivetrain;
    }

    
    public void setDrivetrain(String drivetrain) {
        this.drivetrain = drivetrain;
    }

    
    public String getMotor() {
        return motor;
    }

    
    public void setMotor(String motor) {
        this.motor = motor;
    }

    
    public String getType() {
        return type;
    }

    
    public void setType(String type) {
        this.type = type;
    }

    
    public String getScale() {
        return scale;
    }

    
    public void setScale(String scale) {
        this.scale = scale;
    }
}
