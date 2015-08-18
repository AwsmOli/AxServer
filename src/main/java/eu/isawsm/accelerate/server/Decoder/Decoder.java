package eu.isawsm.accelerate.server.Decoder;

import eu.isawsm.accelerate.server.Passing;
import org.json.JSONException;

/**
 * Created by ofade on 15.08.2015.
 */
public interface Decoder {
    Passing decode(byte[] bytes) throws JSONException;
}
