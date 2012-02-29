/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.utils;

import com.google.gson.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import tangible.utils.exceptions.WrongProtocolJsonSyntaxException;

/**
 *
 * @author leo
 */
public class JsonProtocolHelper {
  
  private static String printableElement(JsonElement elm){
    //System.out.println("printableElement");
    String elmAsString = elm.toString();
    //System.out.println("elmAsString: << "+elmAsString+" )");
    if(elmAsString.length() > 20){
      String tmpString = elmAsString.substring(0,9)+"[...]"
          +elmAsString.substring(elmAsString.length()-5);
      //System.out.println("tmpString : "+tmpString);
      elmAsString = tmpString;
    }
    //TODO perhaps encoding it to avoid JSON syntax exception if we send it...
    return elmAsString;
  }
  
  public static JsonElement assertField(JsonObject elm, String field)
      throws WrongProtocolJsonSyntaxException{
    //System.out.println("assertField");
    if(!elm.has(field)){
      throw new WrongProtocolJsonSyntaxException("the JSON element:<<"
          +printableElement(elm)+">> was excpected to be have a field "
          + "called " + field);
    }
    return elm.get(field);
  }
  
  public static <T> T assertType(JsonElement elm, Class<T> tClass)
      throws WrongProtocolJsonSyntaxException{
    Gson gson = new Gson();
    T t;
    try{
      t = gson.fromJson(elm, tClass);
    }catch(JsonSyntaxException ex){
      Logger.getLogger(JsonProtocolHelper.class.getName()).log(Level.SEVERE, "Gson generate the following error: ", ex);
      throw new WrongProtocolJsonSyntaxException("the JSON element:<<"
          +printableElement(elm)+">> is not parseable into the type: "
          +tClass);
    }
    return t;
  }
  
  public static <T> T assertTypeInObject(JsonObject elm, Class<T> tClass, String field)
      throws WrongProtocolJsonSyntaxException{
    JsonElement subElm = assertField(elm, field);
    return assertType(subElm, tClass);
  }
  
  public static JsonObject assertObject(JsonElement elm) 
      throws WrongProtocolJsonSyntaxException{
    if(!elm.isJsonObject()){
      throw new WrongProtocolJsonSyntaxException("the JSON element:<<"
          +printableElement(elm)+">> was excpected to be an object!");
    }
    return elm.getAsJsonObject();
  }
  
  public static JsonObject assertObjectInObject(JsonObject elm, String field) 
      throws WrongProtocolJsonSyntaxException{
    JsonElement subElm = assertField(elm, field);
    return assertObject(subElm);
  }
  
  public static String assertString(JsonElement elm) throws WrongProtocolJsonSyntaxException{
    String str;
    try{
      str = elm.getAsString();
    }catch (ClassCastException ex){
      throw new WrongProtocolJsonSyntaxException("the JSON element:<<"
          +printableElement(elm)+">> was excpected to be a string!");
    }
    return str;
  }
  
  public static String assertStringInObject(JsonObject elm, String field) throws WrongProtocolJsonSyntaxException{
    JsonElement subElm = assertField(elm, field);
    return assertString(subElm);
  }
  
  public static int assertInt(JsonElement elm)
      throws WrongProtocolJsonSyntaxException{
    int i;
    try{
      i = elm.getAsInt();
    }catch (ClassCastException ex){
      throw new WrongProtocolJsonSyntaxException("the JSON element:<<"
          +printableElement(elm)+">> was excpected to be an integer!");
    }
    return i;
  }
  
  public static int assertIntInObject(JsonObject elm, String field)
      throws WrongProtocolJsonSyntaxException{
    JsonElement subElm = assertField(elm, field);
    return assertInt(subElm);
  }
  
  public static JsonArray assertArray(JsonElement elm)
      throws WrongProtocolJsonSyntaxException{
    if(!elm.isJsonArray()){
      throw new WrongProtocolJsonSyntaxException("the JSON element:<<"
          +printableElement(elm)+">> was excpected to be an array!");
    }
    return elm.getAsJsonArray();
  }
  
  public static JsonArray assertArrayInObject(JsonObject elm, String field) throws WrongProtocolJsonSyntaxException{
    JsonElement subElm = assertField(elm, field);
    return assertArray(subElm);
  }
  
  
  public static JsonElement assertCtrlMsg(JsonElement elm){
    throw new UnsupportedOperationException("Not implemented yet");
  }
  public static JsonElement assertEventMsg(JsonElement elm){
    throw new UnsupportedOperationException("Not implemented yet");
  }
}