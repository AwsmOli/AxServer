package eu.isawsm.accelerate.server;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.skoky.P3tools.data.msg.PassingGeneric;
import com.skoky.P98tools.data.msg.Passing;
import com.skoky.raspi.Converter;
import com.sun.org.apache.xpath.internal.SourceTree;
import eu.isawsm.accelerate.server.model.*;
import junit.framework.Test;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.naming.ConfigurationException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;


/**
 * Basic Echo Client Socket
 */
public class AxServer {

   private SocketIOServer mServer;
   private IClub club;
   private Set<IDriver> drivers;

    public  void start(String[] args){
        System.out.println("AxServer args:");
        for(String s : args){
            System.out.println(s);

            switch (s.toLowerCase()){
                case "-generatetestdata":
                    test();
                    break;
            }
        }

        //Init DecoderLib
        new Converter(args);

        WebSocketClient client = new WebSocketClient(URI.create("ws://127.0.0.1:8080/websocket")) {

            Gson gson = new Gson();
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                System.out.println("RaspiTimingBox Connection open");

            }

            @Override
            public void onMessage(String s) {
                System.out.println("In: " + s);
                if(s.endsWith("RaspiTimingBox connected")) return;

                long time;
                int transponderID;

                try {
                    PassingGeneric passing = gson.fromJson(s, PassingGeneric.class);
                    time = passing.getRtcTime()/1000;
                    transponderID = passing.getTransponder();

                } catch (JsonSyntaxException e) {
                    Passing passing = gson.fromJson(s, Passing.class);
                    time = Long.parseLong(passing.getTimeSecs())*1000;
                    transponderID = Integer.parseInt(passing.getTransponder());
                }

                processLaps(time, transponderID);
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

        server.addEventListener("registerDriver", Object.class, new DataListener<Object>() {
            @Override
            public void onData(SocketIOClient socketIOClient, Object iDriver, AckRequest ackRequest) throws Exception {
                drivers.add((IDriver) iDriver);
                System.out.println("Driver registered: " + ((IDriver) iDriver).getName());
                for (ICar car :((IDriver) iDriver).getCars()){
                    System.out.println(" -" + car.getName());
                }
            }
        });

        server.addEventListener("TestConnection", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) {
                System.out.println("Client connected: " + socketIOClient.getRemoteAddress() + " " + s + " " + ackRequest.toString());
                socketIOClient.sendEvent("Welcome", club);
            }
        });


        server.addConnectListener(socketIOClient -> {
            System.out.println("Client connected: " + socketIOClient.getRemoteAddress());

            socketIOClient.sendEvent("Welcome", club);
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
                    .sendEvent("LapCompleted:" + id, new Lap(endTime - startTime, club.getTracks().get(0).getCourse()));
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
