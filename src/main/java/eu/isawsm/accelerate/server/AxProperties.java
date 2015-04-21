package eu.isawsm.accelerate.server;

import eu.isawsm.accelerate.server.model.*;

import javax.naming.ConfigurationException;
import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

/**
 * Created by ofade_000 on 08.04.2015.
 */
public class AxProperties {

    public Club club;

    public AxProperties() throws ConfigurationException {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            String clubname = prop.getProperty("ClubName");

            if(clubname == null) throw new ConfigurationException("Club Name is not defined in config.properties");

            URI clubURI = URI.create(prop.getProperty("ClubURL", ""));
            String trackType = prop.getProperty("TrackType" ,"");
            String courseName = prop.getProperty("CourseName", SimpleDateFormat.getDateInstance().format(new Date()));
            String condition = prop.getProperty("Condition", "");
            boolean reverse = Boolean.parseBoolean(prop.getProperty("Reverse", "false"));
            long minTime = Long.parseLong(prop.getProperty("MinTime"));
            long maxTime = Long.parseLong(prop.getProperty("MaxTime"));

            Course course = new Course(condition, courseName, reverse, minTime, maxTime);
            Track track = new Track(trackType,course);
            club = new Club(clubname,clubURI,null, Arrays.asList(track));

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
}
