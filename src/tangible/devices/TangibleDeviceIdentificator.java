/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tangible.devices;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import tangible.utils.exceptions.UnSupportedDeviceType;
import tangible.utils.exceptions.WrongProtocolJsonSyntaxException;


/**
 *
 * @author leo
 */
@Deprecated
public class TangibleDeviceIdentificator {
    public static Class getDeviceByType(String type) throws UnSupportedDeviceType{
        if(type.equals("SifteoCubes")){
            return SifteoDevice.class;
        }//TODO add more if cases to find the correct matching type
        
        //If no device was found:
        throw new UnSupportedDeviceType(type);
    }
    
    public static TangibleDevice getDeviceFromJson(JsonObject obj) 
        throws UnSupportedDeviceType, WrongProtocolJsonSyntaxException {
      String type;
      JsonElement elem;
      if(obj.has("type") && (elem = obj.get("type")).isJsonPrimitive()){
        try{
          type = elem.getAsString();
        }catch (ClassCastException ex){
          throw new WrongProtocolJsonSyntaxException("the field 'type' must be a string");
        }
      }else{
        throw new WrongProtocolJsonSyntaxException("device must have a 'type' field");
      }
      //type is initialized now:
      Gson gson = new Gson();
      return (TangibleDevice) gson.fromJson(obj, getDeviceByType(type));
    }
}
