package eu.isawsm.accelerate.server;

import Shared.Club;
import Shared.Course;
import Shared.Track;
import eu.isawsm.accelerate.server.Decoder.Decoder;
import eu.isawsm.accelerate.server.Decoder.MyLapsDecoder;
import eu.isawsm.accelerate.server.Decoder.RobiDecoder;

import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Simply the Config for the Server
 * Created by Awli on 08.04.2015.
 */
public class AxProperties {

    public Club club;
    public int PORT;
    public String COMPORT;
    public Decoder decoder;

    private Properties prop;

    public AxProperties() {
        init();
    }

    private void init(){
        prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            getClub();
            getWifiPort();
            getComPort();
            getDecoder();


        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean check() {
        boolean retVal = true;
        if(club == null) retVal = false;
        if(COMPORT == null) retVal = false;
        if(decoder == null) retVal = false;
        if(PORT == 0) retVal = false;

        return  retVal;
    }

    private void getWifiPort() {
        PORT = Integer.parseInt(prop.getProperty("WifiPort") == null ? "1337" : prop.getProperty("WifiPort"));


    }

    private void store(){
        OutputStream out = null;
        try {
            out = new FileOutputStream("config.properties");
            prop.store(out,"");
            init();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(out !=null)
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private void getComPort() {
        COMPORT = prop.getProperty("COMPort");
        if(COMPORT == null) System.out.println("No COMPort defined in settings!");
    }

    private void getDecoder() {
        String Decoder = prop.getProperty("Decoder");
        switch (Decoder){
            case "MyLaps P98":
                decoder = new MyLapsDecoder(MyLapsDecoder.Version.P98);
                break;
            case "MyLaps P3":
                decoder = new MyLapsDecoder(MyLapsDecoder.Version.P3);
                break;
            case "Robitronic":
                decoder = new RobiDecoder();
            default:
                System.out.println("Unrecognized Decoder in settings: " + Decoder);
        }
    }

    private void getClub() {
        String clubName = prop.getProperty("ClubName");

        if(clubName == null)  System.out.println("Club Name is not defined in config.properties");

        URI clubURI = URI.create(prop.getProperty("ClubURL", ""));
        String trackType = prop.getProperty("TrackType" ,"");
        String courseName = prop.getProperty("CourseName", SimpleDateFormat.getDateInstance().format(new Date()));
        String condition = prop.getProperty("Condition", "");
        boolean reverse = Boolean.parseBoolean(prop.getProperty("Reverse", "false"));
        long minTime = Long.parseLong(prop.getProperty("MinTime"));
        long maxTime = Long.parseLong(prop.getProperty("MaxTime"));

        Course course = new Course(condition, courseName, reverse, minTime, maxTime);
        Track track = new Track(trackType,course);
        club = new Club(clubName,clubURI,null, Collections.singletonList(track));
    }

    public void writeClubName(String text) {
        prop.setProperty("ClubName", text);
        store();
    }

    public void writeMinLapTime(String text) {
        prop.setProperty("MinTime", text);
        store();
    }

    public void writeMaxLapTime(String text) {
        prop.setProperty("MaxTime", text);
        store();
    }

    public void writeWifiPort(String text) {
        prop.setProperty("WifiPort", text);
        store();
    }

    public void writeSerialPort(Object value) {
        prop.setProperty("COMPort", value.toString());
        store();
    }

    public void writeDecoder(Object value) {
        prop.setProperty("Decoder", value.toString());
        store();
    }
}
