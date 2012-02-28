/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package restful.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author leo
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JSONRestResource {
  protected Gson _gson = new Gson();
  
  
  protected String createJsonMsg(Object o, boolean isCtrl){
    JsonObject msg = new JsonObject();
    msg.addProperty("flow", isCtrl? "ctrl": "event");
    if( o instanceof JsonElement){
      msg.add("msg", (JsonElement) o);
    }else{
      msg.add("msg", _gson.toJsonTree(o));
    }
    return _gson.toJson(msg);
  }
  
  protected String createJsonCtrlMsg(Object o){
    return createJsonMsg(o, true);
  }
  
  protected Response createJsonResponseMsg(Object o, boolean isCtrl,Status statusCode){
    return Response.status(statusCode).entity(createJsonMsg(o,isCtrl)).build();
  }
  protected Response createJsonCtrlResponseMsg(Object o, Status statusCode){
    return createJsonResponseMsg(o, true, statusCode);
  }
  
  protected Response createMissingCompulsoryParamMsg(String field){
    return createErrorMsg("Missing a required parameter", field);
  }

  protected Response createErrorMsg(String err_msg, String reason) {
    JsonObject err = new JsonObject();
    err.addProperty("err_msg", err_msg);
    err.addProperty("err_source", reason);
    
    return createJsonCtrlResponseMsg(err, Status.BAD_REQUEST);
    //TODO_LATER maybe move that status to the parameter to enable a specific status code to be set...
  }
  
  protected Response createOKCtrlMsg(){
    return createJsonCtrlResponseMsg("OK", Status.OK);
  }
}
