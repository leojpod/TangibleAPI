/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.utils.exceptions;

/**
 *
 * @author leo
 */
public class WrongProtocolJsonSyntaxException extends WrongProtocolException{
  public final String msg;
  public WrongProtocolJsonSyntaxException(String exception) {
    super("JSON_SYNTAX");
    msg = exception;
  }

  @Override
  public String getMessage() {
    return super.getMessage()+"\n"
        + "msg: "+msg;
  }
  
  
}
