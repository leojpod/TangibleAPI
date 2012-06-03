/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.devices;

import tangible.gateway.GenericTangibleGateway;
import tangible.protocols.TangibleDeviceProtocol;

/**
 *
 * @author leo
 */
public class GenericTangibleDevice {
	private GenericTangibleGateway _gateway;
	private TangibleDeviceProtocol _talk;
	private String _id;
	

	public GenericTangibleDevice(GenericTangibleGateway gateway, String devId) {
		this._gateway = gateway;
		_id = devId;
	}
	
	public GenericTangibleGateway getGateway() {
		return _gateway;
	}
	
	public String getId() {
		return _id;
	}
}
