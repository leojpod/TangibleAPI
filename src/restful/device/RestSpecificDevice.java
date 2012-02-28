/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package restful.device;

import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import managers.DeviceFinder;
import managers.DeviceFinderAccess;
import managers.ReservationManager;
import managers.ReservationManagerAccess;
import restful.utils.ConditionalAccessResource;
import restful.utils.UnauthorizedAccessException;
import tangible.devices.TangibleDevice;
import utils.ColorHelper;

/**
 *
 * @author leo
 */
@Path("/tangibleapi/{appuuid}/device_methods/{device_ID}")
public class RestSpecificDevice extends ConditionalAccessResource {
//public class RestSpecificDevice extends JSONRestResource {
  private DeviceFinder _finder = DeviceFinderAccess.getInstance();
  private ReservationManager _mgr = ReservationManagerAccess.getInstance();
  
  
  public RestSpecificDevice(@PathParam("appuuid") String uuid,
      @PathParam("device_ID") String devID){
    super(uuid, new Condition[] {Condition.APP_REGISTERED});
    //System.out.println("the received uuid is : "+query_uuid);
    if(!_mgr.isAReservation(devID, uuid))
    {
      throw new UnauthorizedAccessException("the device "+devID
          +" is not reseved by the application"+uuid);
    }
  }
  
  @PUT @Path("/show_color")
  public Response showColor(
      @PathParam("device_ID") String devID,
      @PathParam("appuuid") String appUUID,
      @FormParam("r") Integer r_value,
      @FormParam("g") Integer g_value,
      @FormParam("b") Integer b_value,
      @FormParam("color") String color
      ){
    Integer color_value;
    color_value = null;
    System.out.println("trying to change the cubes'color!");
    System.out.println("here are the variables: \n"
        + "\t device_ID "+devID+"\n"
        + "\t color "+color+"\n"
        + "\t appuuid "+appUUID);
    if(appUUID == null){
      return this.createMissingCompulsoryParamMsg("appUUID");
    }
    //let's check that the device is reserved by the application
    if(!_mgr.isAReservation(devID, appUUID)){
      return this.createErrorMsg("the device is not reserved by "
          + "the specified application!", "device: "+devID+" / app: "+appUUID);
    }
    try{
      color_value = Integer.parseInt(color, 16);
    }catch(NumberFormatException ex){
      color_value = null;
    }
    //now we know that the request is legal.
    //let's check if the other params are correct too
    if(r_value == null || g_value == null || b_value == null){
      //one of the component is null ... let's try to use color instead
      if(color_value == null){
        //TODO send an error message
        return this.createMissingCompulsoryParamMsg("a color must be specified "
            + "using the parameters r, g & b or color");
      }else{
        if(ColorHelper.isValidColor(color_value)){
          return this.showColor(color_value, devID);
        }else{
          return this.createErrorMsg("the specified color is not"
              + " a valid representation of the color", color_value.toString());
        }
      }
    }else{
      //let's use the three components to print the color on the cubes!
      if(ColorHelper.isValidColor(r_value, g_value, b_value)){
        return this.showColor(r_value, g_value, b_value, devID);
      }else{
        return this.createErrorMsg("the specified color is not"
            + " a valid representation of the color", 
            "r:"+r_value+" g:"+g_value+" b:"+b_value);
      }
    }
  }
  
  private Response showColor(int rgb, String devID){
    int[] rgb_array = ColorHelper.decompose(rgb);
    return showColor(rgb_array[0], rgb_array[1], rgb_array[2], devID);
    
  }
  private Response showColor(int r, int g, int b, String devID){
    TangibleDevice dev = _finder.getDevice(devID);
    dev.getTalk().showColor(r, g, b);
    return this.createOKCtrlMsg();
  }
  
  @PUT @Path("/subscribe")
  public Response addSubscription(
      //TODO_LATER add a filter here to register only some events
      ){
    //check if there is already a streaming socket for this (appuuid,device) 
    //if()
    //get a free port
    //create a streaming socket on it
    //setup the subscription
    //send back the port
    return null;
  }
}
