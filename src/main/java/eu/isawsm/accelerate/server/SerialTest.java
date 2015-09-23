package eu.isawsm.accelerate.server;

import eu.plib.P3tools.MsgProcessor;
import eu.isawsm.accelerate.server.Readers.SerialReader;
import eu.plib.Ptools.Message;
import gnu.io.UnsupportedCommOperationException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.EmptyByteBuf;
import org.apache.commons.codec.DecoderException;

import java.util.Arrays;
import java.util.Date;

public class SerialTest {
    byte[] buf;

    public static void MyLapsTest(String[] args) throws DecoderException, InterruptedException, UnsupportedCommOperationException {
        SerialReader serialReader = new SerialReader(("COM1"), null, 115200, 8, 1, 0);
        long RTC_TIME = 2842824000l;
        long[] transponder = {1337, 1338, 1339, 1340, 1341, 1342, 1343, 1344, 1345, 1346};

        while (true) {

            for (long l : transponder) {

                String json = "{\"passingNumber\":\"" + l + "\",\"transponder\":\"" + l + "\",\"RTC_Time\":\"" + RTC_TIME + "\",\"strength\":\"45\",\"hits\":\"6C\",\"flags\":\"0\",\"recordType\":\"Passing\",\"crcOk\":true,\"unknownFields\":{\"02\":{\"length\":\"1\",\"data\":\"01\",\"tof\":2}},\"emptyFields\":[],\"VERSION\":\"1\",\"SPARE\":\"0000\",\"FLAGS\":\"0000\",\"decoderId\":\"54020200\"}";
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

    public static void main(String[] args) throws DecoderException, InterruptedException, UnsupportedCommOperationException {

        byte request = -4;
        long transponder1 = 1337;
        long time = new Date().getTime();
        byte hits = 1;
        byte milliHits = 5;

        SerialReader serialReader = new SerialReader(("COM1"), null, 115200, 8, 1, 0);



        while (true) {
            for (byte b2 : addByte(addByte(addLong(addLong(createRequest(request), transponder1), time), hits), milliHits)) {
                serialReader.writeData(b2);
            }
            Thread.sleep(1000);
            time = new Date().getTime();
        }
    }

    private static byte[] createRequest(byte request) {
        byte[] retVal = new byte[3];

        retVal[0] = 3;
        retVal[1] = 0x14; // Vorbesetzung CRC
        retVal[2] = request;

        return retVal;
    }

    private static byte[] addByte(byte[] packet, byte value) {
        byte size = packet[0];

        packet = incSizeof(packet, 1);

        packet[size++] = (byte) (value & 0xff);
        packet[0] = size;

        return packet;
    }

    private static byte[] addInt(byte[] packet, int value) {
        byte size = packet[0];
        packet = incSizeof(packet, 2);

        packet[size++] = (byte) (value & 0xff);
        value = value >> 8;
        packet[size++] = (byte) (value & 0xff);
        packet[0] = size;

        return packet;
    }


    private static byte[] addLong(byte[] packet, long value) {
        byte size = packet[0];

        packet = incSizeof(packet,4);

        packet[size++] = (byte) (value & 0xff);
        value = value >> 8;
        packet[size++] = (byte) (value & 0xff);
        value = value >> 8;
        packet[size++] = (byte) (value & 0xff);
        value = value >> 8;
        packet[size++] = (byte) (value & 0xff);
        packet[0] = size;

        return packet;
    }

    int getInt(int ix) {
        int v = 0;
        v = (v << 8) | buf[ix + 1];
        v = (v << 8) | buf[ix + 0];
        return v;
    }

    long getLong(int ix) {
        long v = 0;
        v = (v << 8) | buf[ix + 3];
        v = (v << 8) | buf[ix + 2];
        v = (v << 8) | buf[ix + 1];
        v = (v << 8) | buf[ix + 0];
        return v;
    }

    static byte[] incSizeof(byte[] array, int i){
        return Arrays.copyOfRange(array, 0, array.length + i);
    }

}
