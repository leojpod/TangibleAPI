/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.protocols.device_speficic.sifteo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import tangible.devices.SifteoCubeDevice;
import tangible.gateway.SiftDriver;
import tangible.protocols.DeviceAuthenticationProtocol.DeviceFoundCallBack;
import tangible.protocols.device_speficic.SpecificAuthenticationProtocol;
import tangible.utils.JsonProtocolHelper;
import tangible.utils.exceptions.UnSupportedDeviceType;

/**
 *
 * @author leo
 */
public class SifteoAuthenticationProtocol implements SpecificAuthenticationProtocol{

  @Override
  public void authenticateDevices(JsonObject obj, Socket s, DeviceFoundCallBack cb) {
    //read information about the siftDriver and create it
    String type = JsonProtocolHelper.assertStringInObject(obj, "type");
    String protocol_version = JsonProtocolHelper.assertStringInObject(obj, "protocolVersion");
    //let's check that just to be sure!
    if(!type.equals("SifteoCubes")){
      throw new UnSupportedDeviceType(type);
    }
    //SiftDriver driver = JsonProtocolHelper.assertType(obj, SiftDriver.class);
    // this parsing is not working so let's try another way...
    String app_Id, driver_id;
    app_Id = JsonProtocolHelper.assertStringInObject(obj, "appMgrId");
    driver_id = JsonProtocolHelper.assertStringInObject(obj, "id");
    SiftDriver driver = new SiftDriver(app_Id, driver_id);
    try {
      //create a communicationProtocol for the driver;
      SiftDriverCommunicationProtocol driver_talk = new SiftDriverCommunicationProtocol(driver, s);
      JsonArray cubesId = JsonProtocolHelper.assertArrayInObject(obj, "cubesId");
      //create them a cube device for each cube id given by the message
      //instanticate them with the siftDriver previously created
      //no need to store the siftDriver: it is living as long as at least one of
      //his cube is present in the system
      for(Iterator<JsonElement> ite = cubesId.iterator(); ite.hasNext();){
        String one_cube_id = JsonProtocolHelper.assertString(ite.next());
        SifteoCubeDevice cube = new SifteoCubeDevice(type, protocol_version, one_cube_id, driver_id);
        if(!cb.callback(cube)){
          throw new UnSupportedDeviceType(cube.type+"##"+cube.id);
        }//else
//        System.out.println("callback successfully excecuted");
        //create a CommunicationProtocol for the cube and attach it!
        SifteoCommunicationProtocol cube_talk = new SifteoCommunicationProtocol(driver_talk,cube.id);
        cube.attachCommunication(cube_talk);
        //add cube to its gateway
        driver.add(cube);
        //that's done for this cube
      }
      driver_talk.startReading();
    } catch (IOException ex) {
      Logger.getLogger(SifteoAuthenticationProtocol.class.getName()).log(Level.SEVERE, null, ex);
    }

  }

}
