package eu.isawsm.accelerate.server.Decoder;

import eu.isawsm.accelerate.server.Passing;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Uses the PLib to Decode outputs from an MyLaps Decoder
 * Created by ofade on 15.08.2015.
 */
public class MyLapsDecoder implements Decoder{

    public enum Version{
        P3,
        P98
    }

    private String name = "MyLaps";

    private com.skoky.Ptools.MsgProcessor msgProcessor;

    /**
     * Initializes the Decoder with the specified protocol version
     * @param version Either P3 or P89
     */
    public MyLapsDecoder(Version version) {
        switch (version){
            case P3:
                name = "MyLaps P3";
                msgProcessor = new com.skoky.P3tools.MsgProcessor();
                break;
            case P98:
                name = "MyLaps P98";
                msgProcessor = new com.skoky.P3tools.MsgProcessor();
                break;
        }
    }

    @Override
    public Passing decode(byte[] bytes) throws JSONException {
        try {

            String JSONMsg = msgProcessor.parse(bytes).toString();
            if(!JSONMsg.contains("passingNumber")) return null;


            JSONObject jsonObject = new JSONObject(JSONMsg);



            long transponderID = Long.parseLong(jsonObject.get("transponder").toString(), 16);
            long time = Long.parseLong(jsonObject.get("RTC_Time").toString(), 16) / 1000;

            return new Passing(time, transponderID, Passing.DecoderType.MyLaps);

        } catch (Exception e) {
            System.out.println("Unable to decode Message: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
