package eu.isawsm.accelerate.server.Readers;
import eu.isawsm.accelerate.server.Decoder.Decoder;
import eu.isawsm.accelerate.server.PassingListener;
import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Serial Helper Class
 * Created by ofade on 14.08.2015.
 */
public class SerialReader implements SerialPortEventListener {

    private SerialPort serialPort = null;
    private Decoder decoder;

    //input and output streams for sending and receiving data
    private InputStream input = null;
    private OutputStream output = null;

    private boolean bConnected = false;

    //the timeout value for connecting with the port
    final static int TIMEOUT = 2000;

    public SerialReader(String Port, Decoder decoder,int speed,int databit,int stopbit,int parity) throws UnsupportedCommOperationException {
        this.decoder = decoder;


        connect(Port);

        if (isConnected()) {
            serialPort.setSerialPortParams(speed, databit,stopbit,parity);
            if (initIOStream()) {
                initListener();
                return;
            }
        }
        throw new IllegalStateException("Cant connect to SerialPort " + Port);
    }

    public boolean isConnected() {
        return bConnected;
    }

    public void setConnected(boolean bConnected) {
        this.bConnected = bConnected;
    }

    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.DATA_AVAILABLE:
                readSerial();
                break;
        }
    }

    private void readSerial(){
        try {
            int availableBytes = input.available();

            while(availableBytes > 0){
                decoder.buffer.put((byte) input.read());
                availableBytes--;
            }
        } catch (IOException e) {
            System.out.println("Failed to read data. (" + e.toString() + ")");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //method that can be called to send data
    //pre style="font-size: 11px;": open serial port
    //post: data sent to the other device
    public void writeData(int leftThrottle) {
        try {
            output.write(leftThrottle);
            output.flush();
        } catch (Exception e) {
            System.out.println("Failed to write data. (" + e.toString() + ")");
        }
    }

    //search for all the serial ports
    //pre style="font-size: 11px;": none
    //post: adds all the found ports to a combo box on the GUI
    public static HashMap<String, CommPortIdentifier> searchForPorts() {
        HashMap<String, CommPortIdentifier> portMap = new HashMap<>();
        Enumeration ports = CommPortIdentifier.getPortIdentifiers();

        while (ports.hasMoreElements()) {
            CommPortIdentifier curPort = (CommPortIdentifier) ports.nextElement();

            //get only serial ports
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                portMap.put(curPort.getName(), curPort);
            }
        }
        return portMap;
    }

    //connect to the selected port in the combo box
    //pre style="font-size: 11px;": ports are already found by using the searchForPorts
    //method
    //post: the connected comm port is stored in commPort, otherwise,
    //an exception is generated
    public void connect(String port) {

        CommPortIdentifier selectedPortIdentifier = SerialReader.searchForPorts().get(port);

        CommPort commPort;

        try {
            //the method below returns an object of type CommPort
            commPort = selectedPortIdentifier.open("TigerControlPanel", TIMEOUT);
            //the CommPort object can be casted to a SerialPort object
            serialPort = (SerialPort) commPort;

            //for controlling GUI elements
            setConnected(true);

            //logging
            System.out.println(port + " opened successfully.");

            //CODE ON SETTING BAUD RATE ETC OMITTED
            //XBEE PAIR ASSUMED TO HAVE SAME SETTINGS ALREADY

        } catch (PortInUseException e) {
            System.out.println(port + " is in use. (" + e.toString() + ")");
        } catch (Exception e) {
            System.out.println("Failed to open " + port + "(" + e.toString() + ")");
        }
    }

    //open the input and output streams
    //pre style="font-size: 11px;": an open port
    //post: initialized input and output streams for use to communicate data
    public boolean initIOStream() {
        try {
            input = serialPort.getInputStream();

            output = serialPort.getOutputStream();

            return true;
        } catch (IOException e) {
            System.out.println("I/O Streams failed to open. (" + e.toString() + ")");
            return false;
        }
    }

    //starts the event listener that knows whenever data is available to be read
    //pre style="font-size: 11px;": an open serial port
    //post: an event listener for the serial port that knows when data is received
    public void initListener() {
        try {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (TooManyListenersException e) {
            System.out.println("Too many listeners. (" + e.toString() + ")");
        }
    }

    //disconnect the serial port
    //pre style="font-size: 11px;": an open serial port
    //post: closed serial port
    public void disconnect() {
        //close the serial port
        try {
            writeData(0);

            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
            setConnected(false);

            System.out.println("Disconnected.");
        } catch (Exception e) {
            System.out.println("Failed to close " + serialPort.getName()
                    + "(" + e.toString() + ")");
        }
    }

    public void addPassingListener(PassingListener passingListener) {
        decoder.addPassingListener(passingListener);
    }
}
