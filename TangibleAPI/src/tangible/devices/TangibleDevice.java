package tangible.devices;

import com.google.gson.annotations.SerializedName;
import java.net.Socket;
import tangible.protocols.TangibleDeviceCommunicationProtocol;

/**
 * provides the basic information about any kind of supported device
 *
 * @author leo
 */
public abstract class TangibleDevice {

  public String type;
  @SerializedName("protocolVersion")
  public String protocol_version;
  public String id;

  public TangibleDevice() {
    type = null;
    protocol_version = null;
    id = null;
  }

  public TangibleDevice(String type, String protocol_version, String id) {
    this.type = type;
    this.protocol_version = protocol_version;
    this.id = id;
  }

//    protected transient Socket _s;
  //private transient TangibleDeviceCommunicationProtocol _talk;
//    public void attachSocket(Socket sock){
//      _s = sock;
//      //_talk = createTangiebleDeviceProtocol(_s);
//    }
  //protected abstract TangibleDeviceCommunicationProtocol createTangiebleDeviceProtocol(Socket sock);
//    public boolean isConnected(){
//      return _s != null && _s.isConnected();
//    }
  public String getHashCode() {
    return type + '.' + id;
  }

  @Deprecated
  public abstract void attachSocket(Socket s);

  public abstract TangibleDeviceCommunicationProtocol<?> getTalk();

  public abstract boolean isConnected();

  /**
   *
   * @param <T> Type of the final device
   * @param talk
   */
  public abstract <T extends TangibleDevice> void attachCommunication(TangibleDeviceCommunicationProtocol<T> talk);
}
