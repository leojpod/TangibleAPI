/* by /Leoj -- /Lekko -- /Lojeuv
 *
 */
package commons;

import javax.ws.rs.core.Response;

/**
 *
 * @author LeoGS
 */
public class ApiException extends RuntimeException{
  public final Response.Status _status;
  public final String _msg;
  public ApiException(Response.Status status, String msg) {
    _status = status;
    _msg = msg;
  }

  @Override
  public String getMessage() {
    return _msg;
  }
  public Response.Status getStatus() {
    return _status;
  }


}
