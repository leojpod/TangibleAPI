/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package restful.device;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import managers.DeviceFinder;
import managers.DeviceFinderAccess;
import managers.ReservationManager;
import managers.ReservationManagerAccess;
import restful.utils.ConditionalAccessResource;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author leo
 */
@Path("/tangibleapi/{appuuid}/device")
public class RestDevice extends ConditionalAccessResource {
//public class RestDevice extends JSONRestResource {
  private DeviceFinder _finder = DeviceFinderAccess.getInstance();
  private ReservationManager _mgr = ReservationManagerAccess.getInstance();
  
  
  public RestDevice(@PathParam("appuuid") String uuid){
    super(uuid, new Condition[] {Condition.APP_REGISTERED});
//    System.out.println("the received uuid is : "+uuid);
  }
  
  @GET public Response getDeviceList(){
    return createJsonCtrlResponseMsg(_finder.getDevices(), Status.OK);
  }
  
  @PUT @Path("/reservation/{deviceId}")
  public Response makeReservationById(
      @PathParam("deviceId") String id,
      @PathParam("appuuid") String appUUID){
    try{
      String reservation = _mgr.reserveDeviceById(id, appUUID);
      return createJsonCtrlResponseMsg(reservation, Status.OK);
    } catch (ReservationManager.UnsuccessfulReservationException ex){
      return createJsonCtrlResponseMsg(ex, Status.CONFLICT);
    }
  }
  @PUT @Path("/reservation")
  public Response makeReservationByCapability(){
    return createJsonCtrlResponseMsg(new NotImplementedException(), Response.Status.SERVICE_UNAVAILABLE);
  }
  
  @DELETE @Path("/reservation/{deviceId}")
  public Response cancelReservation(
      @PathParam("deviceId") String id,
      @PathParam("appuuid") String appUUID){
    try{
      _mgr.endReservation(id, appUUID);
      return Response.ok().build();
    }catch (ReservationManager.NoSuchReservationException ex){
      return createErrorMsg("this reservation doesn't exist", "device: "+id+" / appUUID: "+appUUID);
    }
  }
  
  @GET @Path("/info")
  public Response getInformations(){
    return createJsonCtrlResponseMsg(new NotImplementedException(), Response.Status.SERVICE_UNAVAILABLE);
  }
  
  @GET @Path("/info/{deviceUUID}")
  public Response getDeviceInformation(){
  return createJsonCtrlResponseMsg(new NotImplementedException(), Response.Status.SERVICE_UNAVAILABLE);
  }
}
