package tangible.devices;

import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import tangible.protocols.TangibleDeviceCommunicationProtocol;
import tangible.protocols.device_speficic.sifteo.SifteoCommunicationProtocol;

/**
 *
 * @author leo
 */
@Deprecated
public class SifteoDevice extends TangibleDevice {

  //private int number_of_cubes;
  //private String sifteo_api_version;
  @SerializedName("cubesId")
  private String[] cubes_id;
  private transient SifteoCommunicationProtocol _communicator;


  public int getCubeNumber(){ return cubes_id.length;}
  

  @Override
  public TangibleDeviceCommunicationProtocol getTalk() {
    return _communicator;
  }

  @Override
  public boolean isConnected() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T extends TangibleDevice> void attachCommunication(TangibleDeviceCommunicationProtocol<T> talk) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void attachSocket(Socket s) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
