package Shared;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * Represents a Driver
 * Created by Awli on 29.01.2015.
 */
public class Driver implements Serializable {
   
    private String name;
    private Bitmap image;
    private URI mail;
    private HashSet<Car> cars;

    public Driver(String name, Bitmap image, URI mail) {
        this.name = name;
        this.image = image;
        this.mail = mail;
        cars = new HashSet<>();
    }

    public Driver() {
        cars = new HashSet<>();
        name = "";
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String firstname) {
        this.name = firstname;
    }

    
    public Bitmap getImage() {
        return image;
    }

    
    public void setImage(Bitmap image) {
        this.image = image;
    }

    
    public URI getMail() {
        return mail;
    }

    
    public void setMail(URI mail) {
        this.mail = mail;
    }

    
    public void addCar(Car car) {
        cars.add(car);
    }

    
    public void removeCar(Car car) {
        cars.remove(car);
    }

    
    public HashSet<Car> getCars() {
        return cars;
    }

    
    public void setCars(HashSet<Car> cars) {
        this.cars = cars;
    }

    
    public String toString() {
        return name != null ? name : "";
    }

    public Car getCar(long transponderID) {
        for(Car c : cars){
            if(c.getTransponderID() == transponderID){
                return c;
            }
        }
        return null;
    }
}
