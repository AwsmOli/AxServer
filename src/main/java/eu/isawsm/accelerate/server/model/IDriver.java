package eu.isawsm.accelerate.server.model;

import android.graphics.Bitmap;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by ofade_000 on 04.04.2015.
 */
public interface IDriver {
    String getName();

    void setName(String firstname);

    Bitmap getImage();

    void setImage(Bitmap image);

    URI getMail();

    void setMail(URI mail);

    void addCar(ICar car);

    void removeCar(ICar car);

    ArrayList<ICar> getCars();

    void setCars(ArrayList<ICar> cars);
}
