/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.gateway;

import java.util.Collection;
import tangible.devices.TangibleDevice;
import tangible.protocols.TangibleGatewayCommunicationProtocol;

/**
 *
 * @param <Types> DeviceType that are gathered by this gateway
 * @author leo
 */
public interface TangibleGateway <Types  extends TangibleDevice> extends Collection<Types> {
  public TangibleGatewayCommunicationProtocol<Types> getTalk();
  public String[] getDevicesId();
  public String getId();
	//public void disconnect();
}
