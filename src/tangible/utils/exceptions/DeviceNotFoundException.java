/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.utils.exceptions;

/**
 *
 * @author leo
 */
public class DeviceNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  String id;
  
  public DeviceNotFoundException(String id) {
    this.id = id;
  }

  @Override
  public String getMessage() {
    return "the id:("+id+") did not match any present device";
  }
  
  
  
}
