/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package managers;

import java.util.List;

/**
 *
 * @author leo
 */
public interface ReservationManager {

  public static class UnsuccessfulReservationException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    
  }
  public static class NoSuchReservationException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    
  }
  String reserveDeviceById(String device_id, String app_id) 
      throws UnsuccessfulReservationException;
  String reserveDeviceByType(String type, String app_id) 
      throws UnsuccessfulReservationException;
  List<String> reservedByAnApp(String app_id) 
      throws UnsuccessfulReservationException;
  
  void endReservation(String device_id, String app_id) throws NoSuchReservationException;
  
  boolean isAReservation(String devID, String appUUID);
  
}
