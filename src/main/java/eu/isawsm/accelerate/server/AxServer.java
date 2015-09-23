package eu.isawsm.accelerate.server;

import Shared.Car;
import Shared.Driver;
import Shared.Lap;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;
import eu.isawsm.accelerate.server.Readers.SerialReader;
import eu.isawsm.accelerate.server.UI.MainForm;
import eu.isawsm.accelerate.server.UI.SettingsView;
import gnu.io.UnsupportedCommOperationException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.jmdns.JmmDNS;
import javax.jmdns.ServiceInfo;
import java.io.*;
import java.net.BindException;
import java.util.ArrayList;
import java.util.Date;


/**
 * Basic Echo Client Socket
 */
public class AxServer {

    private AxProperties axProperties = new AxProperties();
    private MainForm mainForm;
    private SocketIOServer mServer;

    private static final ObservableList<Driver> drivers = load();


    private JmmDNS jmDNS = JmmDNS.Factory.getInstance();
    private Stage primaryStage;

    public AxServer(Stage primaryStage) {
        this.primaryStage = primaryStage;

        if(!axProperties.check()){
            showSettings(axProperties);
        }

        startUI();



        new Thread(() -> {
            startSocketServer();
            startSerialReader();
            initZeroNetConf();
        }).start();

    }


    private void startUI()  {
        primaryStage.setOnCloseRequest(event -> shutdown());
        showMainView(axProperties);
    }

    private void showMainView(AxProperties axProperties)  {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainForm.fxml"));
            Parent root = loader.load();
            mainForm = loader.getController();
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle(axProperties.club.getName() + " - Axelerate Server");
            primaryStage.getIcons().add(new Image("ic_launcher.png"));

            mainForm.setStatusText("Connected to " + axProperties.decoder.toString() + " via " + axProperties.COMPORT);
            mainForm.setPrimaryStage(primaryStage);
            mainForm.setProperties(axProperties);

            mainForm.setDrivers(drivers);
            mainForm.addNameChangedListener((car, newName) -> {

            });

            primaryStage.show();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showSettings(AxProperties axProperties) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Settings.fxml"));
            Parent root;

            root = loader.load();

            SettingsView settings = loader.getController();

            settings.setProperties(axProperties);

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Settings - Axelerate Server");
            primaryStage.getIcons().add(new Image("ic_launcher.png"));

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Driver findDriver(Driver driver){
        for(Driver d : drivers){
            for(Car c : d.getCars()){
                for(Car c2 : driver.getCars()){
                    if(c.equals(c2)){
                        return d;
                    }
                }
            }
        }
        return null;
    }

    private void startSocketServer() {
        Configuration config = new Configuration();
        config.setPort(axProperties.PORT);

        try{
            SocketIOServer server = new SocketIOServer(config);
            server.addEventListener("registerDriver", String.class, (socketIOClient, driverJson, ackRequest) -> {
                Driver remoteDriver = new Gson().fromJson(driverJson, Driver.class);
                System.out.println("Driver registered: " + remoteDriver.getName());
                Driver localDriver = findDriver(remoteDriver);

                if(localDriver != null){
                        for(Car remoteCar : remoteDriver.getCars()) {
                            if(!localDriver.getCars().add(remoteCar)) {
                                Car localCar = localDriver.getCar(remoteCar.getTransponderID());
                                localCar.merge(remoteCar);
                            }
                        }

                    localDriver.setName(remoteDriver.getName());
                    localDriver.setImage(remoteDriver.getImage());
                    localDriver.setMail(remoteDriver.getMail());

                    socketIOClient.sendEvent("driverMerged", localDriver);
                } else
                    drivers.add(remoteDriver);
            });

            server.addEventListener("TestConnection", String.class, (socketIOClient, s, ackRequest) -> {
                System.out.println("Client connected: " + socketIOClient.getRemoteAddress() + " " + s + " " + ackRequest.toString());
                socketIOClient.sendEvent("Welcome", axProperties.club);
            });

//            server.addConnectListener(socketIOClient -> {
//                System.out.println("Client connected: " + socketIOClient.getRemoteAddress());
//
//                socketIOClient.sendEvent("Welcome", axProperties.club);
//            });

            server.start();
            mServer = server;
        } catch (Exception e){
            if(e instanceof BindException){
                System.out.println("Port " + axProperties.PORT + " is already in use! Is the AxServer already running?");
                throw e;
            }
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        System.out.println("AxServer up and running on Port " + config.getPort());
    }

    private SerialReader serialReader;
    private void startSerialReader() {
        //This should not be static
        try {
            serialReader = new SerialReader(axProperties.COMPORT, axProperties.decoder, axProperties.serialSpeed, axProperties.serialDataBits, axProperties.serialStopBits, axProperties.serialParity);
            serialReader.addPassingListener(new PassingListener() {
                @Override
                public void onPaassing(Passing passing) {
                    processLaps(passing);
                }
            });
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
        }
    }

    private void initZeroNetConf() {
        try {
            ServiceInfo serviceInfo = ServiceInfo.create("_AxNetConf._tcp.local.",
                    "AndroidTest", axProperties.PORT,
                    "test from android");
            jmDNS.registerService(serviceInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            primaryStage.hide();

            jmDNS.unregisterAllServices();
            jmDNS.close();
            serialReader.disconnect();
            mServer.stop();
            Platform.exit();

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

                //noinspection InfiniteLoopStatement
                while (true) {
                    long time = (long) (Math.random() * 20000 + 1800);

                    if (Math.random() * 10 > 9) {
                        time += 5000;
                    }

                    Platform.runLater(() -> processLaps(new Passing(new Date().getTime(), l, Passing.DecoderType.MyLaps)));

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
        for(Driver d : drivers) {
            for (Car c : d.getCars()) {
                if (c.getTransponderID() == passing.getTransponderID()) {
                    car = c;
                }
            }
        }

        //If its not already there create it
        if(car == null){
            car = new Car(null,null,passing.getTransponderID(),null);
            Driver d = new Driver();
            d.addCar(car);
            drivers.add(d);
        }

        //If it has no currentlap going on set it in a new Current lap with the passing time
        if(car.getCurrentLap() == null ){
            car.setCurrentLap(new Lap (passing.getTimne(),axProperties.club.getTracks().get(0).getCourse()));
        } else {
            //else finish the lap
            Lap finishedLap = car.finishCurrentLap(passing.getTimne());

            if(finishedLap == null) //Max or Min Time check
                return;

            //and send it
            mServer.getBroadcastOperations()
                    .sendEvent("LapCompleted:" + car.getTransponderID(), new Gson().toJson(finishedLap));
        }
        final Car finalCar = car;
        Platform.runLater(() -> mainForm.updateCars(finalCar));

        store(drivers);

    }

    private void store(ObservableList<Driver> drivers) {
        try {
            FileOutputStream fileOut = new FileOutputStream("cars.ser");
            ObjectOutputStream out = null;

            out = new ObjectOutputStream(fileOut);

            out.writeObject(drivers.toArray());
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ObservableList<Driver> load(){
        ObservableList<Driver> retVal = FXCollections.observableArrayList();
        try {
            FileInputStream fileIn = new FileInputStream("cars.ser");
            ObjectInputStream in = null;

            in = new ObjectInputStream(fileIn);

            Object[] drivers = (Object[]) in.readObject();

            in.close();
            fileIn.close();
            for(Object o : drivers){
                retVal.add((Driver)o);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("No saves found creating new file...");
        }
        return retVal;
    }
}
