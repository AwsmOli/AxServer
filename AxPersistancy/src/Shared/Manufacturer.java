package Shared;

import android.graphics.Bitmap;
import android.os.Parcel;

import java.io.Serializable;

/**
 * Represents a Manufacturer
 * Created by Awli on 31.01.2015.
 */
public class Manufacturer implements Serializable {

    private String name;
    private Bitmap image;

    public Manufacturer(String name, Bitmap image) {
        this.name = name;
        this.image = image;
    }

    private Manufacturer(Parcel in) {
        super();
        setName(in.readString());
        setImage((Bitmap) in.readValue(null));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


}
