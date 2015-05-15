package eu.isawsm.accelerate.server;

import Shared.Car;
import Shared.Club;
import Shared.Driver;
import Shared.Lap;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.google.gson.Gson;
import com.skoky.raspi.Converter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import javax.naming.ConfigurationException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Set;


/**
 * Basic Echo Client Socket
 */
public class AxServer {

   private SocketIOServer mServer;
   private Club club;
   private Set<Driver> drivers;

    private boolean verbose = false;

    public  void start(String[] args){
        System.out.println("AxServer args:");
        for(String s : args){
            System.out.println(s);

            switch (s.toLowerCase()){
                case "-generatetestdata":
                    test();
                    break;
                case "-v":
                case "-verbose":
                    verbose = true;
                    break;
            }
        }

        //Init DecoderLib
        startConverter(args);

        WebSocketClient client = new WebSocketClient(URI.create("ws://127.0.0.1:8080/websocket")) {

            Gson gson = new Gson();
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                System.out.println("RaspiTimingBox Connection open");

            }

            @Override
            public void onMessage(String s) {

                if(s.endsWith("RaspiTimingBox connected")) return;
                if(!s.contains("passingNumber")) return;
                if(verbose) System.out.println("In: " + s);
                long time;
                int transponderID;

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    transponderID = Integer.parseInt(jsonObject.get("transponder").toString(), 16);
                    time = Long.parseLong(jsonObject.get("RTC_Time").toString(),16)/1000;

                    processLaps(time, transponderID);
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                System.out.println("Connection Closed");
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        };
        client.connect();


    }

    private void startConverter(String... args){
        try {
            new Converter(args);
        } catch (NullPointerException e){
            //seems to be some bug inside the ConverterLib
            System.out.println("Null in Converter, restarting!");
            startConverter(args);
        }
    }

    private void test(){
        long time = 0;
        while(true){
            time += (Math.random() * 20000);

            processLaps(time, 1337);
            try {
                Thread.sleep(5000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public AxServer() throws ConfigurationException {
        club = new AxProperties().club;
        Configuration config = new Configuration();
        config.setPort(1337);

       final SocketIOServer server = new SocketIOServer(config);

        server.addEventListener("registerDriver", Driver.class, new DataListener<Driver>() {
            @Override
            public void onData(SocketIOClient socketIOClient, Driver driver, AckRequest ackRequest) throws Exception {
                drivers.add(driver);
                System.out.println("Driver registered: " + driver.getName());
                for (Car car :driver.getCars()){
                    System.out.println(" -" + car.getName());
                }
            }
        });

        server.addEventListener("TestConnection", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                System.out.println("Client connected: " + socketIOClient.getRemoteAddress() + " " + s + " " + ackRequest.toString());
                socketIOClient.sendEvent("Welcome", club);
            }
        });

        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                System.out.println("Client connected: " + socketIOClient.getRemoteAddress());

                socketIOClient.sendEvent("Welcome", club);
            }
        });


        server.start();
        mServer = server;



        System.out.println("AxServer up and running on Port " + config.getPort() );

    }



    private static ArrayList<Transponder> transponders = new ArrayList<>();

    private void processLaps(long time, int transponderID) {
        Transponder transponder = null;
        for(Transponder t : transponders){
            if(t.id == transponderID){
                transponder = t;
            }
        }
        if(transponder == null)
            transponders.add(new Transponder(transponderID, time));
        else
            transponder.completeLap(time);
    }


    private class Transponder {
        public int id;
        public long startTime;

        public Transponder(int id, long startTime) {
            this.id = id;
            this.startTime = startTime;
        }

        public void completeLap(long endTime){
            if(startTime == 0) {
                startTime = endTime;
                return;
            }
            System.out.println("OUT:  LapCompleted:" + id + " Time: " + (endTime - startTime));
            mServer.getBroadcastOperations()
                    .sendEvent("LapCompleted:" + id, new Gson().toJson(new Lap(endTime - startTime, club.getTracks().get(0).getCourse())));
            startTime = endTime;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof  Transponder){
                return ((Transponder)obj).id == id;
            }
            return super.equals(obj);
        }
    }
}
