package eu.isawsm.accelerate.server.Decoder;

import eu.isawsm.accelerate.server.Passing;
import eu.isawsm.accelerate.server.PassingListener;
import org.json.JSONException;

import java.util.Arrays;

/**
 *
 * Created by olfad on 19.08.2015.
 */
public class RobiDecoder extends Decoder {

    public static final byte passingRequestID = -4;
    public static final byte secondRequestID = 4;

    public RobiDecoder() {
        super();
    }

    @Override
    public Passing decode(byte[] bytes) throws JSONException {
      if(getRequestType(bytes) == passingRequestID){
          return new Passing(getTime(bytes), getTransponderID(bytes), Passing.DecoderType.Robitronic);
      }

      throw new JSONException("");
    }

    private int size = -1;

    @Override
    protected Thread initThread() {
        return new Thread(() -> {
            byte[] packet = new byte[0];

            while (!stopThread) {
                try {
                    byte b = buffer.take();
                    if (size <= 0) {
                        packet = new byte[0];
                        size = b;
                    }
                    packet = Arrays.copyOfRange(packet, 0, packet.length + 1);
                    packet[packet.length - 1] = b;
                    size--;

                    if (size == 0) {
                        for(PassingListener p : passingListeners){
                            p.onPaassing(decode(packet));
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private byte getRequestType(byte[] telegraph){
       if(telegraph[2] == passingRequestID){
            return passingRequestID;
        } else if(telegraph[2] == secondRequestID){
           return secondRequestID;
       }
        return 0;
    }

    //bytes  7 - 10
    private long getTime(byte[] telegraph){
        return  (telegraph[10]<<24)&0xff000000|
                (telegraph[9]<<16)&0x00ff0000|
                (telegraph[8]<< 8)&0x0000ff00|
                (telegraph[7])&0x000000ff;
    }

    //bytes 3 - 5
    private long getTransponderID(byte[] telegraph){
        return  (telegraph[5]<<16)&0x00ff0000|
                (telegraph[4]<< 8)&0x0000ff00|
                (telegraph[3])&0x000000ff;
    }

    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }


    @Override
    public String toString() {
        return "RobiLap";
    }
}
