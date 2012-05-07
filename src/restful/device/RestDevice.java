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

  @OPTIONS
  public Response getDeviceListOption(
          @HeaderParam("Access-Control-Request-Headers") String requestH){
    return makeCORS(requestH);
  }

  @GET public Response getDeviceList(){
    System.out.println("listing devices");
    return createJsonCtrlResponseMsg(_finder.getDevices(), Status.OK);
  }

  @OPTIONS @Path("/reservation")
  public Response reservationOptions(
          @HeaderParam("Access-Control-Request-Headers") String requestH){
    return makeCORS(requestH);
  }
  @PUT @Path("/reservation")
  public Response makeReservationByCapability(){
    return createJsonCtrlResponseMsg(new NotImplementedException(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @OPTIONS @Path("/reservation/{deviceId}")
  public Response reservationDeviceOptions(
          @HeaderParam("Access-Control-Request-Headers") String requestH){
    return makeCORS(requestH);
  }
  @PUT @Path("/reservation/{deviceId}")
  public Response makeReservationById(
      @PathParam("deviceId") String id,
      @PathParam("appuuid") String appUUID){
    System.out.println("Reserving device #"+id+" for application "+appUUID);
    try{
      String reservation = _mgr.reserveDeviceById(id, _appuuid);
      return createJsonCtrlResponseMsg(reservation, Status.OK);
    } catch (ReservationManager.UnsuccessfulReservationException ex){
      return new RestApiException(ex,true).getResponse();
    }
  }
  @DELETE @Path("/reservation/{deviceId}")
  public Response cancelReservation(
      @PathParam("deviceId") String id,
      @PathParam("appuuid") String appUUID){
    System.out.println("Releasing device #"+id+" hold by app "+appUUID);
    try{
      _mgr.endReservation(id, _appuuid);
      return createJsonResponseMsg(id, true, Status.OK);
    }catch (ReservationManager.NoSuchReservationException ex){
      return createErrorMsg("this reservation doesn't exist", "device: "+id+" / appUUID: "+appUUID);
    }
  }

  @OPTIONS @Path("/info")
  public Response infoOptions(
          @HeaderParam("Access-Control-Request-Headers") String requestH){
    return makeCORS(requestH);
  }
  @GET @Path("/info")
  public Response getInformations(){
    return createJsonCtrlResponseMsg(new NotImplementedException(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @OPTIONS @Path("/info/{deviceUUID}")
  public Response infoDeviceOptions(
          @HeaderParam("Access-Control-Request-Headers") String requestH){
    return makeCORS(requestH);
  }
  @GET @Path("/info/{deviceUUID}")
  public Response getDeviceInformation(){
  return createJsonCtrlResponseMsg(new NotImplementedException(), Response.Status.SERVICE_UNAVAILABLE);
  }
}
