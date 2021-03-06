package eu.isawsm.accelerate.server.Decoder;

import eu.isawsm.accelerate.server.Passing;
import eu.isawsm.accelerate.server.PassingListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Uses the PLib to Decode outputs from an MyLaps Decoder
 * Created by ofade on 15.08.2015.
 */
public class MyLapsDecoder extends Decoder {

    public enum Version {
        P3,
        P98
    }

    private String name = "MyLaps";

    private eu.plib.Ptools.MsgProcessor msgProcessor;

    /**
     * Initializes the Decoder with the specified protocol version
     *
     * @param version Either P3 or P89
     */
    public MyLapsDecoder(Version version) {
        super();
        switch (version) {
            case P3:
                name = "MyLaps P3";
                msgProcessor = new eu.plib.P3tools.MsgProcessor(false);
                break;
            case P98:
                name = "MyLaps P98";
                msgProcessor = new eu.plib.P3tools.MsgProcessor(false);
                break;
        }
    }

    @Override
    public Passing decode(byte[] bytes) throws JSONException {
        try {
            String JSONMsg = msgProcessor.parse(bytes).toString();
            System.out.println(JSONMsg);
            if (!JSONMsg.contains("passingNumber")) return null;

            JSONObject jsonObject = new JSONObject(JSONMsg);

            long transponderID = Long.parseLong(jsonObject.get("transponder").toString(), 16);
            long time = Long.parseLong(jsonObject.get("RTC_Time").toString(), 16) / 1000;

            return new Passing(time, transponderID, Passing.DecoderType.MyLaps);

        } catch (Exception e) {
            System.out.println("Unable to decode Message: " + e.getMessage());
            //  e.printStackTrace();
        }
        return null;
    }

    @Override
    public Thread initThread() {
        return new Thread(() -> {

            byte[] packet = new byte[0];

            while (!stopThread) {
                try {
                    byte b = buffer.take();
                    if (b == -114) {
                        //Beginning of a new Packet
                        packet = new byte[0];
                    }
                    packet = Arrays.copyOfRange(packet, 0, packet.length + 1);
                    packet[packet.length - 1] = b;

                    if (b == -113) {
                        //End of a Packet
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

    @Override
    public String toString() {
        return name;
    }
}
