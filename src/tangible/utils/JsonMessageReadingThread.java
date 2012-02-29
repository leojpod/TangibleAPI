/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leo
 */
public class JsonMessageReadingThread extends StreamReadingThread<BufferedReader> {

  private JsonParser _parser;
  
  public JsonMessageReadingThread(BufferedReader reader) {
    super(reader);
    _parser = new JsonParser();
  }
  
  @Override
  public void read() {
    //read one JsonElement and finish
    JsonElement elm = _parser.parse(_reader);
    //TODO filter the event and control message and add them in their respective stack
    //the driver will take care of reading them
    if(!elm.isJsonObject()){
      Logger.getLogger(JsonMessageReadingThread.class.getName()).log(Level.INFO, "received an incorrect message, let's ignore it");
    }
    //else
    JsonObject msg = elm.getAsJsonObject();
    
  }
  
}
