/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tangible.devices;

import java.net.Socket;
import tangible.protocols.TangibleDeviceCommunicationProtocol;
import tangible.protocols.device_speficic.sphero.SpheroCommunicationProtocol;

/**
 *
 * @author nicklas
 */
public class SpheroDevice extends TangibleDevice
{
    private transient SpheroCommunicationProtocol _talk;
    private String _driver_id;


    public SpheroDevice( String type, String protocol_version, String id, String driver_id )
    {
        super( type, protocol_version, id );
        _driver_id = driver_id;
    }

    public void attachCommunication(  SpheroCommunicationProtocol talk )
    {
        this._talk = talk;
    }

    @Override
    public void attachSocket( Socket s )
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }


    @Override
    public TangibleDeviceCommunicationProtocol<? extends TangibleDevice> getTalk()
    {
        return this._talk;
    }


    @Override
    public boolean isConnected()
    {
        return this._talk.isConnected();
    }


    @Override
    public <T extends TangibleDevice> void attachCommunication( TangibleDeviceCommunicationProtocol<T> talk )
    {
        throw new UnsupportedOperationException( "The communication is not made for this kind of device" );
    }
}
