/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package restful.utils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author leo
 */
public class ApplicationException extends WebApplicationException {
  private static final long serialVersionUID = 1L;
  private final String appuuid;
  public ApplicationException(String appuuid){
    super(Response.status(Response.Status.BAD_REQUEST).build());
    this.appuuid = appuuid;
  }

  @Override
  public String getMessage() {
    return "an error occured conserning the application «"+appuuid+"»";
  }
  
  
}
