package eu.isawsm.accelerate.server;

import Shared.Car;
import Shared.Driver;
import Shared.Lap;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;
import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.xml.internal.ws.api.databinding.MappingInfo;
import eu.isawsm.accelerate.server.Readers.SerialReader;
import eu.isawsm.accelerate.server.UI.MainForm;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONException;

import javax.jmdns.JmmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


/**
 * Basic Echo Client Socket
 */
public class AxServer {

    private AxProperties axProperties;
    private MainForm mainForm;
    private SocketIOServer mServer;

    private static final ObservableList<Car> cars = FXCollections.observableArrayList();

    private JmmDNS jmdns = JmmDNS.Factory.getInstance();

    public AxServer(AxProperties axProperties, MainForm mainForm) {
        this.axProperties = axProperties;
        this.mainForm = mainForm;

        mainForm.setCars(cars);

        new Thread(() -> {
            startSocketServer();
            startSerialReader();
            initZeroNetConf();
        }).start();

        test();
    }

    private void startSocketServer() {
        Configuration config = new Configuration();
        config.setPort(axProperties.PORT);

        final SocketIOServer server = new SocketIOServer(config);

        server.addEventListener("registerDriver", Driver.class, (socketIOClient, driver, ackRequest) -> {
            System.out.println("Driver registered: " + driver.getName());
            for (Car car : driver.getCars()) {
                System.out.println(" -" + car.getName());
            }
        });

        server.addEventListener("TestConnection", String.class, (socketIOClient, s, ackRequest) -> {
            System.out.println("Client connected: " + socketIOClient.getRemoteAddress() + " " + s + " " + ackRequest.toString());
            socketIOClient.sendEvent("Welcome", axProperties.club);
        });

        server.addConnectListener(socketIOClient -> {
            System.out.println("Client connected: " + socketIOClient.getRemoteAddress());

            socketIOClient.sendEvent("Welcome", axProperties.club);
        });

        server.start();
        mServer = server;

        System.out.println("AxServer up and running on Port " + config.getPort());
    }

    private void startSerialReader() {
        //This should not be static
        new SerialReader(axProperties.COMPORT, data -> {
            try {
                Passing passing = axProperties.decoder.decode(data);
                if (passing != null)
                    processLaps(passing);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void initZeroNetConf() {
        try {
            ServiceInfo serviceInfo = ServiceInfo.create("_AxNetConf._tcp.local.",
                    "AndroidTest", axProperties.PORT,
                    "test from android");
            jmdns.registerService(serviceInfo);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void shutdown() {
        try {
            jmdns.unregisterAllServices();
            jmdns.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void test() {
        long[] transponder = {1337, 1338, 1339, 1340, 1341, 1342, 1343, 1344, 1345, 1346 };


        for(long l :transponder){
            new Thread(() -> {
                try {
                    Thread.sleep((long) (Math.random()*20));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (true) {
                    long time = (long) (Math.random() * 20000 + 1800);

                    if (Math.random() * 10 > 9) {
                        time += 5000;
                    }

                    Platform.runLater(() -> processLaps(new Passing(new Date().getTime(), l, Passing.Type.MyLaps)));

                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void processLaps(Passing passing) {
        Car car = null;


        //Search the car that just passed the line
        for (Car c : cars) {
            if (c.getTransponderID() == passing.getTransponderID()) {
                car = c;
            }
        }


        //If its not already there create it
        if(car == null){
            car = new Car(null,null,passing.getTransponderID(),null);
            cars.add(car);
        }

        //If it has no currentlap going on or the Max time is reached, set it in a new Current lap with the passing time
        if(car.getCurrentLap() == null || car.getCurrentLap().getTime() > axProperties.club.getTracks().get(0).getCourse().getMaxTime()){ //Todo give some feedback about max/min times
            car.setCurrentLap(new Lap (passing.getTimne(),axProperties.club.getTracks().get(0).getCourse()));
        } else {
            //If min time is undercut dont count that lap
            if(car.getCurrentLap().getTime() < axProperties.club.getTracks().get(0).getCourse().getMinTime())
                return; //Todo give some feedback

            //else finish the lap
            Lap finishedLap = car.finishCurrentLap(passing.getTimne());
            //and send it
            mServer.getBroadcastOperations()
                    .sendEvent("LapCompleted:" + car.getTransponderID(), new Gson().toJson(finishedLap));
        }
        final Car finalCar = car;
        Platform.runLater(() -> mainForm.updateCars(finalCar));

    }
}
