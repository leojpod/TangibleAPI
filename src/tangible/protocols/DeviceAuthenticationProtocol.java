package tangible.protocols;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import managers.DeviceFinder;
import managers.DeviceFinderAccess;
import tangible.devices.TangibleDevice;
import tangible.devices.TangibleDeviceIdentificator;
import tangible.protocols.device_speficic.ProtocolSelector;
import tangible.protocols.device_speficic.SpecificAuthenticationProtocol;
import tangible.utils.CallBack;
import tangible.utils.JsonProtocolHelper;
import tangible.utils.exceptions.WrongProtocolException;
import tangible.utils.exceptions.WrongProtocolJsonSyntaxException;
import tangible.utils.exceptions.WrongProtocolVersionException;

/**
 *
 * @author leo
 */
public class DeviceAuthenticationProtocol extends AbsJsonTCPProtocol {
  @Deprecated
  private DeviceFinder _finder;
  private String _api_protocol_version;

  public static abstract class DeviceFoundCallBack
    implements CallBack<TangibleDevice, Boolean> {
  }

  @Deprecated
  public DeviceAuthenticationProtocol(Socket s, int timeout)
      throws IOException {
    super(s);
    s.setSoTimeout(timeout);
    _finder = DeviceFinderAccess.getInstance();
    _api_protocol_version = "deprecated_protocol";
  }

  public DeviceAuthenticationProtocol(Socket s, int timeout,
      String api_protocol_version) throws IOException{
    super(s);
    s.setSoTimeout(timeout);
    _api_protocol_version = api_protocol_version;
  }

  @Deprecated
  public TangibleDevice authenticateDevice() throws WrongProtocolException {
    return authenticateDeviceV2();
  }

  public void authenticateDevices(final DeviceFoundCallBack cb)
      throws WrongProtocolException {
    try{
      authenticateDevicesV3(cb);
    } catch(WrongProtocolException ex){
      this.sendJSON(ex);
      throw ex;
    }
  }

  //old version not really handy
  @Deprecated
  private TangibleDevice authenticateDeviceV1() throws WrongProtocolException {
    // <editor-fold defaultstate="collapsed" desc="old crappy code">
    TangibleDevice dev;
    dev = this.readJSON(TangibleDevice.class);
    /*
     * we received the generics information from the device. let's check that
     * the protocol version is correct and then get ready to read the right
     * TangibleDevice implementation
     *
     */
    String api_protocol_version = this._finder.getProperty("discovery_protocol_version");
    if (!dev.protocol_version.equals(api_protocol_version)) {
      WrongProtocolVersionException ex =
          new WrongProtocolVersionException(dev.protocol_version, api_protocol_version);
      Logger.getLogger(_finder.getClass().getName()).log(Level.SEVERE, null, ex);
      this.sendJSON(ex);
      throw ex;
    }
    //The protocol version is the same, phew!
    this.sendJSON("OK_waiting_for_details");
    Class deviceClass = TangibleDeviceIdentificator.getDeviceByType(dev.type);
    try {
      dev = this.readJSON(deviceClass);
    } catch (JsonSyntaxException ex) {
      Logger.getLogger(_finder.getClass().getName()).log(Level.SEVERE, null, ex);
      WrongProtocolJsonSyntaxException protocol_ex = new WrongProtocolJsonSyntaxException(ex.getLocalizedMessage());
      this.sendJSON(protocol_ex);
      throw protocol_ex;
    }
    /*
     * TODO_LATER check that the device can correctly be handled that everything
     * will be fine with it and only then send a JSON answer to the device
     * driver
     */
    return dev;
    // </editor-fold>
  }


  private TangibleDevice authenticateDeviceV2() throws WrongProtocolException {
    JsonElement elem = this.readJSON();
    JsonElement anElem; JsonObject anObj;
//    Logger.getLogger(DeviceAuthenticationProtocol.class.getName()).log(
    //    Level.INFO, "parsed a Json element: {0}", elem.toString());
    TangibleDevice dev = null;
    if(!elem.isJsonObject()){
      WrongProtocolException ex =
          new WrongProtocolJsonSyntaxException("message are expected to be "
          + "Json objects");
      this.sendJsonCtrlMsg(ex);
      throw ex;
    }
    //elem is an Object
    JsonObject obj = elem.getAsJsonObject();
    if((anElem = obj.get("flow")) == null
        || !anElem.isJsonPrimitive()
        || !anElem.getAsString().equals("ctrl")){
      WrongProtocolException ex =
          new WrongProtocolJsonSyntaxException("The middleware expected a "
          + "control message");
      this.sendJsonCtrlMsg(ex);
      throw ex;
    }
    if((anElem = obj.get("msg"))== null || !anElem.isJsonObject()){
      WrongProtocolException ex =
          new WrongProtocolJsonSyntaxException("the field msg is expected "
          + "to be a Json objects");
      this.sendJsonCtrlMsg(ex);
      throw ex;
    }
    anObj = anElem.getAsJsonObject();
    return TangibleDeviceIdentificator.getDeviceFromJson(anObj);
  }

  public void finalizeAuthentication() {
    finalizeAuthenticationV2();
  }

  private void finalizeAuthenticationV2(){
    JsonObject obj = new JsonObject();
    obj.add("success", new JsonPrimitive(true));
    obj.add("msg", new JsonPrimitive("OK_Authentication_successful"));
    this.sendJsonCtrlMsg(obj);
  }


  private void authenticateDevicesV3(DeviceFoundCallBack cb)
      throws WrongProtocolException {
    JsonElement elem = this.readJSON();
//    Logger.getLogger(DeviceAuthenticationProtocol.class.getName()).log(
//        Level.INFO, "parsed a Json element: {0}", elem.toString());

    //check that we received a msg:
    JsonObject jsonMsg = JsonProtocolHelper.assertObject(elem);

    //check that this is actually a msg of type ctrl
    String flow = JsonProtocolHelper.assertStringInObject(jsonMsg, "flow");
    if(!flow.equals("ctrl")){
      throw new WrongProtocolJsonSyntaxException("The middleware expected a "
          + "control message");
    }

    //proceed the authentication of the devices including the
    //  sifteo communicator and the cubes for instance
    JsonObject content
        = JsonProtocolHelper.assertObjectInObject(jsonMsg, "msg");
    String type = JsonProtocolHelper.assertStringInObject(content, "type");

    //check the API version
    String version
        = JsonProtocolHelper.assertStringInObject(content, "protocolVersion");
    if(!version.equals(_api_protocol_version)){
      throw new WrongProtocolVersionException(version, _api_protocol_version);
    }

    SpecificAuthenticationProtocol spec_protocol = ProtocolSelector.getAuthenticationProtocol(type);
    spec_protocol.authenticateDevices(content, this._sock, cb);
    //NOTE: I am not sure that sending the socket here is the best idea but that
    //will do for now...
    //and we are good
    //but just before that we need to tell the device that the authentication worked fine
    finalizeAuthentication();
//    System.out.println("and we are good, "
//        + "the devices authentication is done for this one!");
  }


}
