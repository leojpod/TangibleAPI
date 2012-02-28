/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.devices;

import java.net.Socket;
import tangible.protocols.device_speficic.sifteo.SifteoCommunicationProtocol;
import tangible.protocols.TangibleDeviceCommunicationProtocol;

/**
 *
 * @author leo
 */
public class SifteoCubeDevice extends TangibleDevice{
  //private transient SiftDriver _driver;
  private transient SifteoCommunicationProtocol _talk;
  private String _driver_id;

  public SifteoCubeDevice(String type, String protocol_version, String id, String driver_id) {
    super(type, protocol_version, id);
    _driver_id = driver_id;
  }
  
  
  @Override
  public TangibleDeviceCommunicationProtocol<SifteoCubeDevice> getTalk() {
    return _talk;
  }
  
  
  public void attachCommunication(SifteoCommunicationProtocol talk){
    _talk = talk;
  }
  
  @Override
  public <T extends TangibleDevice> void 
      attachCommunication(TangibleDeviceCommunicationProtocol<T> talk) {
    throw new UnsupportedOperationException(
        "The communication is not maid for this kind of device");
  }

  @Override
  public boolean isConnected() {
    return _talk.isConnected();
  }

  @Override
  public void attachSocket(Socket s) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  
}
