package eu.isawsm.accelerate.server;

import eu.plib.P3tools.MsgProcessor;
import eu.isawsm.accelerate.server.Readers.SerialReader;
import eu.plib.Ptools.Message;
import gnu.io.UnsupportedCommOperationException;
import org.apache.commons.codec.DecoderException;
import java.util.Arrays;

/**
 * Created by ofade on 15.08.2015.
 */
public class SerialTest {
    public static void main(String[] args) throws DecoderException, InterruptedException, UnsupportedCommOperationException {
        SerialReader serialReader = new SerialReader(("COM1"), null, 115200,8,1,0);
        long RTC_TIME = 2842824000l;
        long[] transponder = {1337, 1338, 1339, 1340, 1341, 1342, 1343, 1344, 1345, 1346 };

        while(true) {

            for(long l : transponder) {

                String json = "{\"passingNumber\":\""+l+"\",\"transponder\":\""+l+"\",\"RTC_Time\":\"" + RTC_TIME + "\",\"strength\":\"45\",\"hits\":\"6C\",\"flags\":\"0\",\"recordType\":\"Passing\",\"crcOk\":true,\"unknownFields\":{\"02\":{\"length\":\"1\",\"data\":\"01\",\"tof\":2}},\"emptyFields\":[],\"VERSION\":\"1\",\"SPARE\":\"0000\",\"FLAGS\":\"0000\",\"decoderId\":\"54020200\"}";
                Message m = new MsgProcessor(false).parseJson(json);
                byte[] b = new MsgProcessor(false).build(m);

                System.out.println("Writing: " + Arrays.toString(b));
                for (byte b2 : b) {
                    serialReader.writeData(b2);
                }
                RTC_TIME += +1000;
                Thread.sleep(1000);
            }
        }
    }

}
