/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.protocols.device_speficic;

import com.google.gson.JsonObject;
import java.net.Socket;
import tangible.protocols.DeviceAuthenticationProtocol;

/**
 *
 * @author leo
 */
public interface SpecificAuthenticationProtocol {
  public void authenticateDevices(JsonObject obj, Socket s, DeviceAuthenticationProtocol.DeviceFoundCallBack cb);
}
