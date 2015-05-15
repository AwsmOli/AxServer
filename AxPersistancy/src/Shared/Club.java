package Shared;

import android.graphics.Bitmap;

import java.net.URI;
import java.util.List;

/**
 * Represents a Club
 * Created by Awli on 31.01.2015.
 */
public class Club  {
    private String name;
    private URI url;
    private Bitmap image;
    private List<Track> tracks;

    public Club(String name, URI url, Bitmap image, List<Track> tracks) {
        this.name = name;
        this.url = url;
        this.image = image;
        this.tracks = tracks;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public URI getUrl() {
        return url;
    }

    
    public void setUrl(URI url) {
        this.url = url;
    }

    
    public Bitmap getmage() {
        return image;
    }

    
    public void setmage(Bitmap image) {
        this.image = image;
    }

    
    public List<Track> getTracks() {
        return tracks;
    }

    
    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}
