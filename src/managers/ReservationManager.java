/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package managers;

import java.util.List;
import java.util.UUID;
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author leo
 */
public interface ReservationManager {

  public static class UnsuccessfulReservationException extends WebApplicationException{
    private static final long serialVersionUID = 1L;
    
  }
  public static class NoSuchReservationException extends WebApplicationException{
    private static final long serialVersionUID = 1L;
    
  }
  String reserveDeviceById(String device_id, UUID app_id) 
      throws UnsuccessfulReservationException;
  String reserveDeviceByType(String type, UUID app_id) 
      throws UnsuccessfulReservationException;
  List<String> reservedByAnApp(UUID app_id) 
      throws UnsuccessfulReservationException;
  
  void endReservation(String device_id, UUID app_id) throws NoSuchReservationException;
  
  boolean isAReservation(String devID, UUID appUUID);
  
}
