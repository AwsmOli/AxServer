package eu.isawsm.accelerate.server.Decoder;

import eu.isawsm.accelerate.server.Passing;
import org.json.JSONException;

/**
 *
 * Created by olfad on 19.08.2015.
 */
public class RobiDecoder implements Decoder {

    public static final byte passingRequestID = 3;
    public static final byte secondRequestID = 4;

    @Override
    public Passing decode(byte[] bytes) throws JSONException {
      if(getRequestType(bytes) == passingRequestID){
          return new Passing(getTime(bytes), getTransponderID(bytes), Passing.DecoderType.Robitronic);
      }

      throw new JSONException("");
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
        return  (telegraph[7]<<24)&0xff000000|
                (telegraph[8]<<16)&0x00ff0000|
                (telegraph[9]<< 8)&0x0000ff00|
                (telegraph[10])&0x000000ff;
    }

    //bytes 3 - 5
    private long getTransponderID(byte[] telegraph){
        return  (telegraph[3]<<16)&0x00ff0000|
                (telegraph[4]<< 8)&0x0000ff00|
                (telegraph[5])&0x000000ff;
    }

    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }


}
