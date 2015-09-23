package eu.isawsm.accelerate.server.Decoder;

import eu.isawsm.accelerate.server.Passing;
import eu.isawsm.accelerate.server.PassingListener;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by ofade on 15.08.2015.
 */
public abstract class Decoder {
    public Decoder() {
        processingThread = initThread();
        runProcessingThread();
    }

    protected Thread processingThread;

    protected boolean stopThread = false;

    public BlockingDeque<Byte> buffer = new LinkedBlockingDeque<>();

    public abstract Passing decode(byte[] bytes) throws JSONException;

    public void runProcessingThread() {
        stopThread = false;
        processingThread.start();
    }
    public void stop(){
        stopThread = true;
    }


    protected abstract Thread initThread();

    protected List<PassingListener> passingListeners = new ArrayList<>();

    public void addPassingListener(PassingListener passingListener) {
        passingListeners.add(passingListener);
    }
}
