/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.gateway;

import com.google.gson.annotations.SerializedName;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import managers.DeviceFinder;
import managers.DeviceFinderAccess;
import tangible.devices.SifteoCubeDevice;
import tangible.devices.TangibleDevice;
import tangible.protocols.TangibleGatewayCommunicationProtocol;
import tangible.protocols.device_speficic.sifteo.SiftDriverCommunicationProtocol;

/**
 *
 * @author leo
 */
public class SiftDriver implements TangibleGateway<SifteoCubeDevice>{


  // <editor-fold defaultstate="collapsed" desc="json serializable fields">
  @SerializedName("appMgrId")
  private String _app_id;
  @SerializedName("type")
  private String _type;
  @SerializedName("id")
  private String _driver_id;
  @SerializedName("cubesId")
  private String[] _cubes_id;
  @SerializedName("protocolVersion")
  private String _protocol_version;
  // </editor-fold>



  private transient List<SifteoCubeDevice> _devices;
  private transient SiftDriverCommunicationProtocol _talk;

  public SiftDriver() {
    _devices = new LinkedList<SifteoCubeDevice>();
  }

  public SiftDriver(String app_id, String driver_id) {
    this();
    this._app_id = app_id;
    this._driver_id = driver_id;
  }



  @Override
  public TangibleGatewayCommunicationProtocol<SifteoCubeDevice> getTalk() {
    return _talk;
  }

  @Override
  public String[] getDevicesId() {
    String[] ids = new String[this.size()];
    int i = 0;
    for(Iterator<SifteoCubeDevice> ite = this.iterator(); ite.hasNext(); i++){
      ids[i] = ite.next().id;
    }
    return ids;
  }

  @Override
  public int size() {
    return _devices.size();
  }

  @Override
  public boolean isEmpty() {
    return _devices.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return _devices.contains(o);
  }

  @Override
  public Iterator<SifteoCubeDevice> iterator() {
    return _devices.iterator();
  }

  @Override
  public Object[] toArray() {
    return _devices.toArray();
  }

  @Override
  public <T> T[] toArray(T[] ts) {
    return _devices.toArray(ts);
  }

  @Override
  public boolean add(SifteoCubeDevice e) {
    return _devices.add(e);
  }

  @Override
  public boolean remove(Object o) {
    return _devices.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> clctn) {
    return _devices.containsAll(clctn);
  }

  @Override
  public boolean addAll(Collection<? extends SifteoCubeDevice> clctn) {
    return _devices.addAll(clctn);
  }

  @Override
  public boolean removeAll(Collection<?> clctn) {
    return _devices.removeAll(clctn);
  }

  @Override
  public boolean retainAll(Collection<?> clctn) {
    return _devices.retainAll(clctn);
  }

  @Override
  public void clear() {
    _devices.clear();
  }

  @Override
  public String getId() {
    return _driver_id;
  }

	public void handleDisconnection(){
		DeviceFinder devMgr = DeviceFinderAccess.getInstance();
		for(TangibleDevice dev : _devices){
			devMgr.removeDevice(dev);
		}
	}

	public void attachCommunication(SiftDriverCommunicationProtocol driver_talk) {
		_talk = driver_talk;
	}
}
