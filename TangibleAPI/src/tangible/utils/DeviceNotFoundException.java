/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.utils;

/**
 *
 * @author leo
 */
class DeviceNotFoundException extends RuntimeException {
  String id;
  
  public DeviceNotFoundException(String id) {
    this.id = id;
  }

  @Override
  public String getMessage() {
    return "the id:("+id+") did not match any present device";
  }
  
  
  
}
